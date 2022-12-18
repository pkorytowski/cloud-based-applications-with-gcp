package com.gcp.uploader.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue( strategy=GenerationType.AUTO )
    long id;

    String email;

    @Builder.Default()
    int images_count = 0;
}
