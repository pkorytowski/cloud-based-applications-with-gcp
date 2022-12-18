package com.gcp.uploader.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FileDto {
    private String fileName;
    private String fileUrl;
}
