package com.gcp.uploader.repository;


import com.gcp.uploader.data.Image;
import com.gcp.uploader.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> getImagesByUser(User user);
}
