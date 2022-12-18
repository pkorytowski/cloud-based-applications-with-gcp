package com.gcp.uploader.data;

import com.gcp.uploader.data.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue( strategy=GenerationType.AUTO )
    long id;

    String image;

    @ManyToOne(targetEntity = User.class)
    @Builder.Default
    User user = null;
}
