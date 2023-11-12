package com.joel.todo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserEntityDto extends CreateUserEntityDto {

    @NotNull
    private Long userId;
}
