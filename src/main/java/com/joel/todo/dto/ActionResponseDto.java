package com.joel.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionResponseDto extends CreateActionDto {

    private Long id;

    private Timestamp addedAt;

    private Timestamp updatedAt;
}
