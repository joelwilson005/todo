package com.joel.todo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

// Entity annotation indicates that this class is a JPA entity
@Entity
// @Data is a Lombok annotation to create all the getters, setters, equals, hash, and toString methods, based on the fields
@Data
// @AllArgsConstructor is a Lombok annotation to generate a constructor with one parameter for each field in your class. Fields marked with @NonNull result in null checks on those parameters
@AllArgsConstructor
// @NoArgsConstructor is a Lombok annotation to generate a constructor with no parameters
@NoArgsConstructor
public class Action {

    // @Id and @GeneratedValue are used to auto-generate the primary key for the entity
    @Id
    @GeneratedValue
    private Long id;

    // Text description of the action
    private String textDescription;

    // Boolean field to check if the action is completed or not
    private boolean completed;

    // @CreationTimestamp is used to automatically set the created at time when we create the entity
    // @Column(updatable = false) is used to ensure that the creation timestamp can't be updated
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp addedAt;

    // Timestamp for when the action was last updated
    private Timestamp updatedAt;

    // @ManyToOne is used to establish a many-to-one relationship with the TodoList entity
    @ManyToOne
    private TodoList todoList;
}