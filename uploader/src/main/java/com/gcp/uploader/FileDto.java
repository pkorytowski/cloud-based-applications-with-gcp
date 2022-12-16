package com.gcp.uploader;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Data
@AllArgsConstructor
public class FileDto {
    private String fileName;
    private String fileUrl;
}
