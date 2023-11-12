package com.joel.todo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoList {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    private Timestamp dueDate;

    private boolean completed;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    private Timestamp updatedAt;

    @ManyToOne
    private UserEntity userEntity;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Action> actionList = new ArrayList<>();

}
