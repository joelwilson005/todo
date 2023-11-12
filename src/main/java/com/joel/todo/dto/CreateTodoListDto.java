package com.joel.todo.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTodoListDto {

    @NotBlank
    private String title;

    private String description;

    private Timestamp dueDate;
}
