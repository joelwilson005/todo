package com.joel.todo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.joel.todo.util.Encrypt;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;


@Entity
@Data
@AllArgsConstructor
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue
    private Long userId;

    @Convert(converter = Encrypt.class)
    private String emailAddress;


    @Convert(converter = Encrypt.class)
    private String firstName;

    @Convert(converter = Encrypt.class)
    private String lastName;

    private Date dateOfBirth;

    private Gender gender;

    // Fields needed for Spring Security and implementation of the UserDetails interface.
    @JsonIgnore
    private String password;

    @Convert(converter = Encrypt.class)
    private String username;

    private AccountStatus accountStatus;

    private LocalDateTime statusSetDate;

    @ManyToMany
    private Set<Role> authorities;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime timeCreated;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TodoList> todoLists = new ArrayList<>();

    @Convert(converter = Encrypt.class)
    private Integer resetPasswordToken;

    public UserEntity() {

        super();

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.authorities;

    }

    @Override
    public boolean isAccountNonExpired() {
        return accountStatus != AccountStatus.EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountStatus != AccountStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return accountStatus != AccountStatus.CREDENTIALS_EXPIRED;
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE;
    }

}

