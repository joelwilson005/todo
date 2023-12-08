package com.joel.todo.repository;

import com.joel.todo.model.TodoList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    Page<TodoList> findAllByUserEntity_UserId(Long userId, Pageable pageable);

    List<TodoList> findAllByUserEntity_UserId(Long userId);

}
