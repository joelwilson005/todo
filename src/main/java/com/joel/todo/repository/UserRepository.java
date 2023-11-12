package com.joel.todo.repository;

import com.joel.todo.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByEmailAddress(String emailAddress);

    Optional<UserEntity> findEntityByUsername(String username);

    Optional<UserEntity> findByResetPasswordToken(Integer token);

    List<UserEntity> findByAccountStatusAndStatusSetDateBefore(String deleted, LocalDate dateToDelete);

}
