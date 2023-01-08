package com.gcp.uploader.service;


import com.gcp.uploader.data.Image;
import com.gcp.uploader.data.User;
import com.gcp.uploader.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public Image save(Image image) {
        return imageRepository.save(image);
    }

    public List<Image> getImagesByUser(User user) {
        return imageRepository.getImagesByUser(user);
    }

}
