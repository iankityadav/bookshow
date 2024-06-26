package com.api.bookshow.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String username;
    private String email;
    private String phone;
    private String dob;
    private String password;
    private Integer role;
}
