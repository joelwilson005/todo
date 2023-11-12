package com.joel.todo.controller;

import com.joel.todo.dto.ActionResponseDto;
import com.joel.todo.dto.CreateTodoListDto;
import com.joel.todo.dto.TodoListResponseDto;
import com.joel.todo.dto.UpdateTodoListDto;
import com.joel.todo.model.TodoList;
import com.joel.todo.service.TodoListService;
import com.joel.todo.util.ActionMapper;
import com.joel.todo.util.TodoListMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/users/{userId}/todos", "/users/{userId}/todos/"})
public class TodoListController {

    private final TodoListService todoListService;
    private final TodoListMapper todoListMapper;
    private final ActionMapper actionMapper;

    @Autowired
    public TodoListController(TodoListService todoListService, TodoListMapper todoListMapper, ActionMapper actionMapper) {

        this.todoListService = todoListService;
        this.todoListMapper = todoListMapper;
        this.actionMapper = actionMapper;

    }

    // Endpoint to get paginated todo lists for a user
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<?> getTodoLists(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "1") int size, @PathVariable Long userId) {

        Sort sort = Sort.by(Sort.Order.asc("createdAt"));
        Page<TodoList> todoListPage;
        Pageable paging = PageRequest.of(page, size, sort);
        todoListPage = this.todoListService.getTodoListRepository().findAllByUserEntity_UserId(userId, paging);

        int currentPage = todoListPage.getNumber();
        int totalNumberOfPages = todoListPage.getTotalPages();
        long totalNumberOfItems = todoListPage.getTotalElements();
        boolean hasPreviousPage = todoListPage.hasPrevious();
        boolean hasNextPage = todoListPage.hasNext();

        if (totalNumberOfItems == 0) {

            return new ResponseEntity<>("No todolist available", HttpStatus.NO_CONTENT);

        }

        if (page > totalNumberOfPages || size > totalNumberOfItems) {

            throw new IndexOutOfBoundsException("Out of bounds.");

        }

        List<TodoList> todoLists = todoListPage.getContent();

        // Map each todoList to a todoListResponseDto and add it to a List<TodoListResponseDto>
        List<TodoListResponseDto> todoListResponseDtoList = new ArrayList<>();
        todoLists.forEach(todoList -> {

            // Map each action in the todolist to an actionResponseDto
            @SuppressWarnings("DuplicatedCode") List<ActionResponseDto> actionResponseDtoList = new ArrayList<>();

            todoList.getActionList().forEach(action -> {

                ActionResponseDto actionResponseDto = new ActionResponseDto();

                this.actionMapper.mapActionToActionResponseDto(action, actionResponseDto);
                actionResponseDtoList.add(actionResponseDto);

            });

            TodoListResponseDto todoListResponseDto = new TodoListResponseDto();
            this.todoListMapper.mapTodoListToTodoListResponseDto(todoList, todoListResponseDto);

            // Set the list of ActionResponseDto to the todoListResponseDto
            todoListResponseDto.setActionList(actionResponseDtoList);

            todoListResponseDtoList.add(todoListResponseDto);

        });


        // Prepare response data including pagination information
        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", currentPage);
        response.put("totalNumberOfPages", totalNumberOfPages);
        response.put("totalNumberOfItems", totalNumberOfItems);
        response.put("hasPreviousPage", hasPreviousPage);
        response.put("hasNextPage", hasNextPage);
        response.put("data", todoListResponseDtoList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint to create a new todo list for a user
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<TodoListResponseDto> createNewTodoList(@PathVariable Long userId, @RequestBody @Valid CreateTodoListDto createTodoListDto) {

        // Map createTodoListDto to todoList
        TodoList todoList = new TodoList();
        this.todoListMapper.mapCreateTodoListDtoToTodoList(createTodoListDto, todoList);

        // Persist the todoList
        todoList = this.todoListService.addTodoListToUser(userId, todoList);

        // Map todoList to todoListResponseDto
        TodoListResponseDto todoListResponseDto = new TodoListResponseDto();
        this.todoListMapper.mapTodoListToTodoListResponseDto(todoList, todoListResponseDto);

        return new ResponseEntity<>(todoListResponseDto, HttpStatus.CREATED);

    }

    // Endpoint to get a specific todo list by ID for a user
    @GetMapping(value = {"/{todoId}", "/{todoId}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<TodoListResponseDto> getTodoListById(@PathVariable Long userId, @PathVariable Long todoId) {

        // Find todoList
        TodoList todoList = this.todoListService.findTodoListById(userId, todoId);

        // Map each action in the todolist to an actionResponseDto
        @SuppressWarnings("DuplicatedCode") List<ActionResponseDto> actionResponseDtoList = new ArrayList<>();
        todoList.getActionList().forEach(action -> {

            ActionResponseDto actionResponseDto = new ActionResponseDto();

            this.actionMapper.mapActionToActionResponseDto(action, actionResponseDto);
            actionResponseDtoList.add(actionResponseDto);

        });

        // Map todoList to todoListResponseDto
        TodoListResponseDto todoListResponseDto = new TodoListResponseDto();
        this.todoListMapper.mapTodoListToTodoListResponseDto(todoList, todoListResponseDto);

        // Add actionResponseDtoList to todoListResponseDto
        todoListResponseDto.setActionList(actionResponseDtoList);

        return new ResponseEntity<>(todoListResponseDto, HttpStatus.OK);

    }

    // Endpoint to update a specific todo list by ID for a user
    @PutMapping(value = {"/{todoId}", "/{todoId}/"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<TodoListResponseDto> updateTodoList(@PathVariable Long userId, @PathVariable Long todoId, @RequestBody UpdateTodoListDto updateTodoListDto) {

        // Map updateTodoListDto to todoListPartial
        TodoList todoListPartial = new TodoList();
        this.todoListMapper.mapUpdateTodoListDtoToTodoList(updateTodoListDto, todoListPartial);

        // Persist updated todoListPartial
        TodoList todoListInDb = this.todoListService.updateTodoList(userId, todoId, todoListPartial);

        // Map todoListInDb to todoListResponseDto
        TodoListResponseDto todoListResponseDto = new TodoListResponseDto();
        this.todoListMapper.mapTodoListToTodoListResponseDto(todoListInDb, todoListResponseDto);

        return new ResponseEntity<>(todoListResponseDto, HttpStatus.OK);

    }

    // Endpoint to delete a specific todo list by ID for a user
    @DeleteMapping(value = {"/{todoId}", "/{todoId}/"})
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<?> deleteTodoList(@PathVariable Long userId, @PathVariable Long todoId) {

        // Delete the todo list
        this.todoListService.deleteTodoList(userId, todoId);

        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);

    }
}