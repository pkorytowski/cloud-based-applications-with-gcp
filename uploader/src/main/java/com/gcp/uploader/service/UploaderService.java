package com.gcp.uploader.service;


import com.gcp.uploader.data.FileDto;
import com.gcp.uploader.data.Image;
import com.gcp.uploader.data.User;
import com.gcp.uploader.pubsub.PubSubPublisher;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UploaderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploaderService.class);
    @Value("${spring.cloud.gcp.project-id}")
    private String gcpProjectId;

    @Value("${gcp-bucket}")
    private String gcpBucketId;

    private final PubSubPublisher uploaderPublisher;
    private final UserService userService;
    private final ImageService imageService;

    @Transactional
    public void upload(MultipartFile file, String email) {

            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            User user = userService.getUserByEmail(email);
            if (user == null) {
                user = userService.addUser(email);
            }



            Path path = new File(originalFileName).toPath();

            FileDto fileDto = null;
            try {
                String contentType = Files.probeContentType(path);
                fileDto = uploadFile(file, originalFileName, contentType);
            } catch (IOException e) {

            }

            if (fileDto != null) {
                user.setImages_count(user.getImages_count() + 1);
                user = userService.save(user);
                Image image = imageService.save(Image.builder()
                        .user(user)
                        .image(fileDto.getFileName())
                        .build());

                try {
                    uploaderPublisher.publishMessage(createPubSubAttributeMap(image), "");
                } catch (Exception e) {
                    LOGGER.error("Error during sending pubsub message: {}", e.getMessage());
                }
            }
    }

    private Map<String, String> createPubSubAttributeMap(Image image) {
        Map<String, String> attrMap = new HashMap<>();

        attrMap.put("email", image.getUser().getEmail());
        attrMap.put("images_count", Integer.toString(image.getUser().getImages_count()));
        attrMap.put("name", image.getImage());
        return attrMap;
    }

    public FileDto uploadFile(MultipartFile multipartFile, String fileName, String contentType) {

        try{
            byte[] fileData = FileUtils.readFileToByteArray(convertFile(multipartFile));

            //InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();

            StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                    .setCredentials(GoogleCredentials.getApplicationDefault()).build();

            Storage storage = options.getService();
            Bucket bucket = storage.get(gcpBucketId,Storage.BucketGetOption.fields());

            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();

            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            Blob blob = bucket.create(fileName + "-" + generatedString + checkFileExtension(fileName), fileData, contentType);

            if(blob != null){
                LOGGER.debug("File successfully uploaded to GCS");

                return new FileDto(blob.getName(), blob.getMediaLink());
            }

        }catch (Exception e){
            LOGGER.error("An error occurred while uploading data. Exception: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private File convertFile(MultipartFile file) {

        try{
            if(file.getOriginalFilename() == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            File convertedFile = new File(file.getOriginalFilename());
            FileOutputStream outputStream = new FileOutputStream(convertedFile);
            outputStream.write(file.getBytes());
            outputStream.close();
            LOGGER.debug("Converting multipart file : {}", convertedFile);
            return convertedFile;
        }catch (Exception e){
            LOGGER.error("Converting failed. Exception: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String checkFileExtension(String fileName) {
        if(fileName != null && fileName.contains(".")){
            String[] extensionList = {".png", ".jpeg", "jpg"};

            for(String extension: extensionList) {
                if (fileName.endsWith(extension)) {
                    //LOGGER.debug("Accepted file type : {}", extension);
                    return extension;
                }
            }
        }
        //LOGGER.error("Not a permitted file type");
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
    }

}
