package com.alissontfraga.subscriptiontracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alissontfraga.subscriptiontracker.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}

