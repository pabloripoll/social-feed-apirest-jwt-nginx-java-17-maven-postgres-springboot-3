package com.restapi.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restapi.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods can be added here
}