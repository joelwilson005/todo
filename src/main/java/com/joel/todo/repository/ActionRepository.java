package com.joel.todo.repository;

import com.joel.todo.model.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {}
