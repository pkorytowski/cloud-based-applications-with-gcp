package com.gcp.uploader.service;


import com.gcp.uploader.data.User;
import com.gcp.uploader.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User addUser(String email) {
        return userRepository.save(User.builder()
                                   .email(email)
                                   .build());
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

}
