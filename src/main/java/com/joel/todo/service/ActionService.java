package com.joel.todo.service;

import com.joel.todo.model.Action;
import com.joel.todo.repository.ActionRepository;
import com.joel.todo.repository.TodoListRepository;
import com.joel.todo.repository.UserRepository;
import com.joel.todo.util.ActionMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ActionService {

    private final ActionRepository actionRepository;
    private final UserRepository userRepository;
    private final TodoListRepository todoListRepository;
    private final ActionMapper actionMapper;

    @Autowired
    public ActionService(ActionRepository actionRepository, UserRepository userRepository, TodoListRepository todoListRepository, ActionMapper actionMapper) {

        this.actionRepository = actionRepository;
        this.userRepository = userRepository;
        this.todoListRepository = todoListRepository;
        this.actionMapper = actionMapper;

    }

    public Action addAction(Long userId, Long todoListId, Action action) {

        // First check to see if user and todoList exists
        if (this.userRepository.findById(userId).isPresent() && this.todoListRepository.findById(todoListId).isPresent()) {

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            action.setAddedAt(timestamp);
            action.setUpdatedAt(timestamp);
            this.todoListRepository.findById(todoListId).orElseThrow().getActionList().add(action);

            return this.actionRepository.save(action);

        } else {

            throw new NoSuchElementException("UserEntity or TodoList may not exist");
        }

    }

    public Action findActionById(Long userId, Long todoId, Long actionId) {
        if (this.userRepository.findById(userId).isPresent() && this.todoListRepository.findById(todoId).isPresent() && this.actionRepository.findById(actionId).isPresent()) {

            return this.actionRepository.findById(actionId).get();

        } else {

            throw new NoSuchElementException("Unable to locate user, todo or action.");

        }
    }

    public Action updateAction(Long userId, Long todoId, Long actionId, Action actionPartial) {

        if (this.userRepository.findById(userId).isPresent() && this.todoListRepository.findById(todoId).isPresent() && this.actionRepository.findById(actionId).isPresent()) {

            Action actionInDb = this.actionRepository.findById(actionId).orElseThrow();

            this.actionMapper.mapActionPartialToAction(actionPartial, actionInDb);

            return this.actionRepository.save(actionInDb);

        } else {

            throw new NoSuchElementException("Unable to locate user, todo or action.");
        }
    }

    public void deleteAction(Long userId, Long todoId, Long actionId) {

        if (this.userRepository.findById(userId).isPresent() && this.todoListRepository.findById(todoId).isPresent() && this.actionRepository.findById(actionId).isPresent()) {

            // Get the todoList repository, remove then remove the action
            Action action = this.actionRepository.findById(actionId).orElseThrow();
            this.todoListRepository.findById(todoId).orElseThrow().getActionList().remove(action);

            this.actionRepository.deleteById(actionId);


        } else {

            throw new NoSuchElementException("Unable to locate user, todo or action.");
        }
    }
}
