package com.joel.todo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

// @Entity annotation indicates that this class is a JPA entity
@Entity
// @Data is a Lombok annotation to create all the getters, setters, equals, hash, and toString methods, based on the fields
@Data
// @AllArgsConstructor is a Lombok annotation to generate a constructor with one parameter for each field in your class. Fields marked with @NonNull result in null checks on those parameters
@AllArgsConstructor
public class Role implements GrantedAuthority {

    // @Id and @GeneratedValue are used to auto-generate the primary key for the entity
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long roleId;

    // Authority field to store the role's authority
    private String authority;

    // Default constructor
    public Role() {
        super();
    }
}