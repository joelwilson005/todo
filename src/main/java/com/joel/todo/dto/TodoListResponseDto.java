package com.joel.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoListResponseDto extends CreateTodoListDto {

    private Long id;

    private Timestamp updatedAt;

    private Timestamp createdAt;

    private boolean completed;

    private List<ActionResponseDto> actionList;
}
