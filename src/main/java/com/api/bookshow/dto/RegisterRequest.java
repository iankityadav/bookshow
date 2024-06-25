package com.api.bookshow.dto;

public class RegisterRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private Integer role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String lastname, String email, String password, Integer role) {
        this.name = name;
        this.username = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
