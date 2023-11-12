package com.joel.todo.dto;

import com.joel.todo.model.AccountStatus;
import com.joel.todo.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityResponseDto {

    private Long userId;

    private AccountStatus accountStatus;

    private ZonedDateTime timeCreated;

    private String firstName;

    private String lastName;

    private String emailAddress;

    private String username;

    private Date dateOfBirth;

    private Gender gender;

}
