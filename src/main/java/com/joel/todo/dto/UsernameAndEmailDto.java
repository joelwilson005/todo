package com.joel.todo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UsernameAndEmailDto {

    @Pattern(regexp = "^[a-z0-9_-]{3,16}$")
    private String username;

    @Email
    private String emailAddress;
}
