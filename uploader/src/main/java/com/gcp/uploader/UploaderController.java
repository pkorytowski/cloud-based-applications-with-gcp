package com.gcp.uploader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/data")
public class UploaderController {

    UploaderService uploaderService;

    @Autowired
    UploaderController(UploaderService uploaderService) {
        this.uploaderService = uploaderService;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void uploadImage(@RequestParam("file")MultipartFile file) {
        uploaderService.upload(file);
    }


}
