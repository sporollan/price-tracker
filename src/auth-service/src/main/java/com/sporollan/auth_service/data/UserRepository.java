package com.sporollan.auth_service.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sporollan.auth_service.model.User;


public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
}
