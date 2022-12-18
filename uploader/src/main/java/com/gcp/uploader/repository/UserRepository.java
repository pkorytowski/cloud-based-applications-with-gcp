package com.gcp.uploader.repository;

import com.gcp.uploader.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //List<User> getUsers();
    User getUserByEmail(String email);
}
