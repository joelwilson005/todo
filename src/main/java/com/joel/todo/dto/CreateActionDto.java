package com.joel.todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateActionDto {

    @NotBlank
    private String textDescription;

    private boolean completed = false;

}
