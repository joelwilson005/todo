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
import java.util.NoSuchElementException;

@Service
@Transactional
public class TodoListService {

    @Getter
    private final TodoListRepository todoListRepository;
    private final UserRepository userRepository;
    private final TodoListMapper todoListMapper;

    private final ActionRepository actionRepository;

    @Autowired
    public TodoListService(TodoListRepository todoListRepository, UserRepository userRepository, TodoListMapper todoListMapper, ActionRepository actionRepository) {

        this.todoListRepository = todoListRepository;
        this.userRepository = userRepository;
        this.todoListMapper = todoListMapper;
        this.actionRepository = actionRepository;

    }

    public TodoList addTodoListToUser(Long userId, TodoList todoList) {

        if (this.userRepository.findById(userId).isPresent()) {

            todoList.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            todoList.setCompleted(false);

            todoList.setUserEntity(this.userRepository.findById(userId).orElseThrow());

            return this.todoListRepository.save(todoList);

        } else {

            throw new NoSuchElementException("User does not exist.");

        }
    }

    public TodoList findTodoListById(Long userId, Long todoId) {

        // First find the user then check to see if the todoList exists.
        if (this.userRepository.findById(userId).isPresent() && this.todoListRepository.findById(todoId).isPresent()) {

            return this.todoListRepository.findById(todoId).get();

        } else {

            throw new NoSuchElementException("Unable to locate UserEntity and/or TodoList");

        }
    }

    public TodoList updateTodoList(Long userId, Long todoId, TodoList todoListPartial) {

        if (this.userRepository.findById(userId).isPresent() && this.todoListRepository.findById(todoId).isPresent()) {

            // Find todoListInDb in db
            TodoList todoListInDb = this.todoListRepository.findById(todoId).orElseThrow();

            // Map todoListPartial values to todoListInDb
            this.todoListMapper.mapTodoListPartialTodoList(todoListPartial, todoListInDb);

            todoListInDb.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            this.todoListRepository.save(todoListInDb);

            return todoListInDb;

        } else {

            throw new NoSuchElementException("UserEntity of TodoList does not exist");

        }
    }

    public void deleteTodoList(Long userId, Long todoId) {

        if (this.userRepository.findById(userId).isPresent() && this.todoListRepository.findById(todoId).isPresent()) {

            TodoList todoList = this.todoListRepository.findById(todoId).orElseThrow();

            // Delete every action in the todoList
            todoList.getActionList().forEach(action -> this.actionRepository.deleteById(action.getId()));

            this.todoListRepository.deleteById(todoId);

        } else {

            throw new NoSuchElementException("UserEntity or TodoList does not exist.");

        }
    }

}
