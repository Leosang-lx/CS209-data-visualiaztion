package com.example.springproject.domain;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class UserForm {
    @NotBlank(message = "lUsername shouldn't be null")
    private String username;
    @Length(min = 6, message = "Password need at least 6 bits")
    private String password;
    @NotBlank(message = "Confirm password shouldn't be null")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}