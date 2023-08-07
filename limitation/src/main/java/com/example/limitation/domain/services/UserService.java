package com.example.limitation.domain.services;

import com.example.limitation.domain.user.UserModel;
import com.example.limitation.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Optional<UserModel> getUser(UUID id) {
        return userRepository.findById(id);
    }
}
