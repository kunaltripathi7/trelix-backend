package com.trelix.trelix_app.service;

import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't Exist for the given Id"));
    }
}




