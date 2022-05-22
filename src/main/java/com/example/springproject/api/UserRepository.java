package com.example.springproject.api;

import com.example.springproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findUserByUsername(String username);
    public User findUserByUsernameAndPassword(String username, String password);
}