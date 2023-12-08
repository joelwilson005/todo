package com.joel.todo.dto;

import com.joel.todo.model.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserEntityDto {

    @NotBlank(message = "Firstname cannot be blank.")
    @Pattern(regexp = "^[A-Za-z-']{1,50}(\\s[A-Za-z-']{1,50})?$")
    private String firstName;

    @NotBlank(message = "Lastname cannot be blank.")
    @Pattern(regexp = "^[A-Za-z-']{1,50}(\\s[A-Za-z-']{1,50})?$")
    private String lastName;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email address is not valid.")
    private String emailAddress;


    private String username;


    /*
    Should have:
     1 lowercase letter,
     1 uppercase letter,
     1 number,
     1 special character
     and be at least 8 characters long
     */
    @Pattern(regexp = "(?=(.*[0-9]))((?=.*[A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z]))^.{8,}$", message = "Invalid password")
    private String password;

    @NotNull(message = "Date of birth cannot be blank.")
    @Past(message = "Date of birth must be in the past.")
    private Date dateOfBirth;

    @NotNull
    private Gender gender;

}


