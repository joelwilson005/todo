// Package declaration for the controller class
package com.joel.todo.controller;

import com.joel.todo.dto.ActionResponseDto;
import com.joel.todo.dto.CreateActionDto;
import com.joel.todo.dto.UpdateActionDto;
import com.joel.todo.model.Action;
import com.joel.todo.service.ActionService;
import com.joel.todo.service.TodoListService;
import com.joel.todo.util.ActionMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// Controller class for handling actions related to todo lists
@CrossOrigin(origins = {"https://silly-alfajores-7fb4bf.netlify.app/"}, allowCredentials = "true")
@RestController
@RequestMapping({"/users/{userId}/todos/{todoId}/actions", "/users/{userId}/todos/{todoId}/actions/"})
public class ActionController {

    // Service dependencies and mapper
    private final ActionService actionService;
    private final TodoListService todoListService;
    private final ActionMapper actionMapper;

    // Constructor to inject dependencies
    @Autowired
    public ActionController(ActionService actionService, TodoListService todoListService, ActionMapper actionMapper) {
        this.actionService = actionService;
        this.todoListService = todoListService;
        this.actionMapper = actionMapper;
    }

    // Endpoint to get all actions in a todo list
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<?> getAllActionsInTodoList(@PathVariable Long userId, @PathVariable Long todoId) {

        // Check if the todo list is empty and return appropriate response
        if (this.todoListService.findTodoListById(userId, todoId).getActionList().isEmpty()) {
            // If the list is empty, return NO_CONTENT status.
            return new ResponseEntity<>("Empty list", HttpStatus.NO_CONTENT);
        } else {
            // If the list is not empty, map actions to ActionResponseDto and return OK status.
            List<Action> actionList = this.todoListService.findTodoListById(userId, todoId).getActionList();
            List<ActionResponseDto> actionResponseDtoList = new ArrayList<>();
            actionList.forEach(action -> {
                ActionResponseDto actionResponseDto = new ActionResponseDto();
                this.actionMapper.mapActionToActionResponseDto(action, actionResponseDto);
                actionResponseDtoList.add(actionResponseDto);
            });
            return new ResponseEntity<>(actionResponseDtoList, HttpStatus.OK);
        }
    }

    // Endpoint to add a new action to a todo list
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<ActionResponseDto> addAction(@PathVariable Long userId, @PathVariable Long todoId, @RequestBody @Valid CreateActionDto createActionDto) {

        // Map CreateActionDto to Action entity
        Action action = new Action();
        this.actionMapper.mapCreateActionDtoToAction(createActionDto, action);

        // Save the action to the database
        action = this.actionService.addAction(userId, todoId, action);

        // Map Action entity to ActionResponseDto and return CREATED status
        ActionResponseDto actionResponseDto = new ActionResponseDto();
        this.actionMapper.mapActionToActionResponseDto(action, actionResponseDto);

        return new ResponseEntity<>(actionResponseDto, HttpStatus.CREATED);

    }

    // Endpoint to get a specific action in a todo list
    @GetMapping(value = {"/{actionId}", "/{actionId}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<ActionResponseDto> getAction(@PathVariable Long userId, @PathVariable Long todoId, @PathVariable Long actionId) {

        // Find the specific action in the todo list
        Action action = this.actionService.findActionById(userId, todoId, actionId);

        // Map Action entity to ActionResponseDto and return OK status
        ActionResponseDto actionResponseDto = new ActionResponseDto();
        this.actionMapper.mapActionToActionResponseDto(action, actionResponseDto);

        return new ResponseEntity<>(actionResponseDto, HttpStatus.OK);

    }

    // Endpoint to update a specific action in a todo list
    @PutMapping(value = {"/{actionId}", "/{actionId}/"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<ActionResponseDto> updateAction(@PathVariable Long userId, @PathVariable Long todoId, @PathVariable Long actionId, @RequestBody @Valid UpdateActionDto updateActionDto) {

        // Map UpdateActionDto to Action entity
        Action actionPartial = new Action();
        this.actionMapper.mapUpdateActionDtoToAction(updateActionDto, actionPartial);

        // Update the action in the database and map it to ActionResponseDto
        Action actionInDb = this.actionService.updateAction(userId, todoId, actionId, actionPartial);
        ActionResponseDto actionResponseDto = new ActionResponseDto();
        this.actionMapper.mapActionToActionResponseDto(actionInDb, actionResponseDto);

        // Return the updated ActionResponseDto with OK status
        return new ResponseEntity<>(actionResponseDto, HttpStatus.OK);

    }

    // Endpoint to delete a specific action from a todo list
    @DeleteMapping(value = {"/{actionId}", "/{actionId}/"})
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<?> deleteAction(@PathVariable Long userId, @PathVariable Long todoId, @PathVariable Long actionId) {

        // Delete the action from the database and return OK status
        this.actionService.deleteAction(userId, todoId, actionId);

        return new ResponseEntity<>("Action successfully deleted", HttpStatus.OK);
    }
}
