package com.example.munichway.services;

import com.example.munichway.DTO.UserCreateRequest;
import com.example.munichway.models.User;
import com.example.munichway.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserCreateRequest request) {
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());

        return userRepository.save(newUser);
    }
}