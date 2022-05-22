package com.example.springproject.service;

import com.example.springproject.domain.User;

public interface UserService {
    public void save(User user);
    public boolean checkLogin(User user);
    public User findByUsername(String username);
}
