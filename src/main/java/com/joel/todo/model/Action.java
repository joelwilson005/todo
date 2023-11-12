package com.joel.todo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action {

    @Id
    @GeneratedValue
    private Long id;

    private String textDescription;

    private boolean completed;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp addedAt;

    private Timestamp updatedAt;

    @ManyToOne
    private TodoList todoList;
}
