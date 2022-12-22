package com.gcp.uploader.controller;

import com.gcp.uploader.service.UploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class UploaderController {

    private final UploaderService uploaderService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void uploadImage(@RequestParam("file")MultipartFile file,
                            @RequestParam("email") String email) {
        uploaderService.upload(file, email);
    }

    @GetMapping("/{email}")
    public List<String> getAllByEmail(@PathVariable String email) {

        return uploaderService.getAllByEmail(email);
    }

    @GetMapping("/file/{filename}")
    public ResponseEntity<byte[]> getImageById(@PathVariable("filename") String imageId) {
        return uploaderService.getImageById(imageId);
    }




}
