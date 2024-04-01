package com.example.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.User;

public interface UserRepository extends JpaRepository<User, UUID>{

	User findByEmail(String email);

	boolean existsByEmail(String email);
}
