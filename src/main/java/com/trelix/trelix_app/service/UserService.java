package com.trelix.trelix_app.service;

import com.trelix.trelix_app.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User findById(UUID id);

}
