package com.joel.todo.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NotBlank
    private String username;

    /*
    Should have:
    1 lowercase letter,
    1 uppercase letter,
    1 number, 1
    special character
    and be at least 8 characters long
  */
    @Pattern(regexp = "(?=(.*[0-9]))((?=.*[A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z]))^.{8,}$", message = "Invalid password")
    private String password;

}
