package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.entity.Role;
import com.codecool.solarwatch.model.entity.UserEntity;
import com.codecool.solarwatch.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    public void promoteToAdmin(String username) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);

        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();

            Set<Role> oldRoles = userEntity.getRoles();

            Set<Role> newRoles = new HashSet<>(oldRoles);

            userEntity.setRoles(newRoles);
            userRepository.save(userEntity);
        } else {
            throw new NoSuchElementException("User not found");
        }
    }
}
