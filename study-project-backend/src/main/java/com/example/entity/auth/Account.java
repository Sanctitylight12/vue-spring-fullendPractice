package com.example.entity.auth;

import lombok.Data;

@Data
public class Account {
    int id;
    String username;
    String password;
    String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
