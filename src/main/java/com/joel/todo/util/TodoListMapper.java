package com.joel.todo.util;

import com.joel.todo.dto.CreateTodoListDto;
import com.joel.todo.dto.TodoListResponseDto;
import com.joel.todo.dto.UpdateTodoListDto;
import com.joel.todo.model.TodoList;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TodoListMapper {

    void mapCreateTodoListDtoToTodoList(CreateTodoListDto createTodoListDto, @MappingTarget TodoList todoList);

    void mapTodoListToTodoListResponseDto(TodoList todoList, @MappingTarget TodoListResponseDto todoListResponseDt);

    void mapUpdateTodoListDtoToTodoList(UpdateTodoListDto updateTodoListDto, @MappingTarget TodoList todoList);

    void mapTodoListPartialTodoList(TodoList todoListPartial, @MappingTarget TodoList todoList);
}
