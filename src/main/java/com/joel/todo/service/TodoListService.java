package com.joel.todo.service;

import com.joel.todo.model.TodoList;
import com.joel.todo.repository.ActionRepository;
import com.joel.todo.repository.TodoListRepository;
import com.joel.todo.repository.UserRepository;
import com.joel.todo.util.TodoListMapper;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;

// @Service annotation is used to indicate that this class is a Spring service component
@Service
// @Transactional annotation is used to ensure that the method or class is executed within a transaction scope
@Transactional
public class TodoListService {

    // Repositories and mapper used in this service
    @Getter
    private final TodoListRepository todoListRepository;
    private final UserRepository userRepository;
    private final TodoListMapper todoListMapper;
    private final ActionRepository actionRepository;

    // @Autowired annotation is used for automatic dependency injection
    @Autowired
    public TodoListService(TodoListRepository todoListRepository, UserRepository userRepository, TodoListMapper todoListMapper, ActionRepository actionRepository) {
        this.todoListRepository = todoListRepository;
        this.userRepository = userRepository;
        this.todoListMapper = todoListMapper;
        this.actionRepository = actionRepository;
    }

    // Method to add a TodoList to a User
    public TodoList addTodoListToUser(Long userId, TodoList todoList) {
        // Check if the user exists
        if (this.userRepository.findById(userId).isPresent()) {
            // Set additional properties and save the todoList
            todoList.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            todoList.setCompleted(false);
            todoList.setUserEntity(this.userRepository.findById(userId).orElseThrow());
            return this.todoListRepository.save(todoList);
        } else {
            throw new NoSuchElementException("User does not exist.");
        }
    }

    // Method to find a TodoList by its ID
    public TodoList findTodoListById(Long userId, Long todoId) {
        // Check if both user and todoList exist
        if (this.userRepository.findById(userId).isPresent() && this.todoListRepository.findById(todoId).isPresent()) {
            return this.todoListRepository.findById(todoId).get();
        } else {
            throw new NoSuchElementException("Unable to locate UserEntity and/or TodoList");
        }
    }

    // Method to update a TodoList
    public TodoList updateTodoList(Long userId, Long todoId, TodoList todoListPartial) {
        // Check if both user and todoList exist
        if (this.userRepository.findById(userId).isPresent() && this.todoListRepository.findById(todoId).isPresent()) {
            // Get the existing todoList, map partial changes, and save
            TodoList todoListInDb = this.todoListRepository.findById(todoId).orElseThrow();
            this.todoListMapper.mapTodoListPartialTodoList(todoListPartial, todoListInDb);
            todoListInDb.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            this.todoListRepository.save(todoListInDb);
            return todoListInDb;
        } else {
            throw new NoSuchElementException("UserEntity of TodoList does not exist");
        }
    }

    // Method to get all TodoLists for a User
    public List<TodoList> getAllTodoLists(Long userId) {
        return this.todoListRepository.findAllByUserEntity_UserId(userId);
    }

    // Method to delete a TodoList
    public void deleteTodoList(Long userId, Long todoId) {
        // Check if both user and todoList exist
        if (this.userRepository.findById(userId).isPresent() && this.todoListRepository.findById(todoId).isPresent()) {
            // Delete associated actions and then delete the todoList
            TodoList todoList = this.todoListRepository.findById(todoId).orElseThrow();
            todoList.getActionList().forEach(action -> this.actionRepository.deleteById(action.getId()));
            this.todoListRepository.deleteById(todoId);
        } else {
            throw new NoSuchElementException("UserEntity or TodoList does not exist.");
        }
    }
}
