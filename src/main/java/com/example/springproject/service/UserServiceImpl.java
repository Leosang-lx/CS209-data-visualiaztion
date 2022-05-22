package com.example.springproject.service;

import com.example.springproject.api.UserRepository;
import com.example.springproject.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public void save(User user) {
        userRepository.save(user);
    }
    @Override
    public boolean checkLogin(User user) {
        User u = userRepository.findUserByUsernameAndPassword(user.getUsername(),user.getPassword());
        return u != null;
    }
    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }
}
