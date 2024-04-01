package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.models.TokenUser;
import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Data
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public String createUser(UserRequestDto request){
        if (repository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already taken");
        }
        if (request.firstName() == null || request.lastName() == null ||
                request.email() == null || request.password() == null){
            throw new IllegalArgumentException("Empty field");
        }
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.getRoles().add(Role.ROLE_USER);

        repository.save(user);

        return user.getId().toString();
    }

    public UserDto getUser(String id) {
        User user = repository.findById(UUID.fromString(id)).orElseThrow(() ->
                new IllegalArgumentException("Incorrect id"));

        return new UserDto(user.getId().toString(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public void updateUser(Authentication authentication, UserRequestDto requestDto) {
        User user = authenticationGetUser(authentication);
        copyCredentials(user, requestDto);

        repository.save(user);
    }

    public void deleteUser(Authentication authentication) {
        User user = authenticationGetUser(authentication);

        repository.delete(user);
    }

    private void copyCredentials(User user, UserRequestDto request){
        if (!request.firstName().isEmpty()){
            user.setFirstName(request.firstName());
        }
        if (!request.lastName().isEmpty()){
            user.setLastName(request.lastName());
        }
        if (!request.email().isEmpty()){
            user.setEmail(request.email());
        }
    }

    public User authenticationGetUser(Authentication authentication){
        User user;
        try {
            user = (User) authentication.getPrincipal();
        } catch (ClassCastException exception){
            log.info("User authenticate with token");
            TokenUser tokenUser = (TokenUser) authentication.getPrincipal();
            user = repository.findByEmail(tokenUser.getUsername());
        }
        return user;
    }
}
