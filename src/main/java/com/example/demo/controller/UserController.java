package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserIdDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/registration")
    public ResponseEntity<?> saveUser(@RequestBody UserRequestDto requestDto) {
        String response;

        try {
            response = userService.createUser(requestDto);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(new UserIdDto(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable(name = "id") String id){
        UserDto userDto;
        try {
            userDto = userService.getUser(id);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(Authentication authentication,
                                             @RequestBody UserRequestDto requestDto){
        try {
            userService.updateUser(authentication, requestDto);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(Authentication authentication){
        try {
            userService.deleteUser(authentication);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
