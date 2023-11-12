package com.joel.todo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityResponseWithTokenDto extends UserEntityResponseDto {

    private String token;

}
