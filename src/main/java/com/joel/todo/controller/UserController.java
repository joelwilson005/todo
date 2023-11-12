package com.joel.todo.controller;

import com.joel.todo.dto.*;
import com.joel.todo.exception.NotUniqueException;
import com.joel.todo.model.UserEntity;
import com.joel.todo.service.UserService;
import com.joel.todo.util.UserMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Controller class for handling user-related operations.
@RestController
@RequestMapping({"/users", "/users/"})
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {

        this.userService = userService;
        this.userMapper = userMapper;

    }

    // Endpoint for user registration.
    @PostMapping(value = {"/register", "/register/"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntityResponseWithTokenDto> createUser(@RequestBody @Valid CreateUserEntityDto createUserEntityDto) throws NotUniqueException {

        // Map createUserEntity to userEntity
        UserEntity userEntity = new UserEntity();
        this.userMapper.mapCreateUserEntityDtoToUserEntity(createUserEntityDto, userEntity);

        // Get plaintext password.
        final String password = createUserEntityDto.getPassword();

        // Save userEntity
        this.userService.createUserEntity(userEntity);

        // Call the userLogin method with the username and password
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(createUserEntityDto.getUsername());
        loginDto.setPassword(password);

        // Call the userLogin method within the same controller

        return userLogin(loginDto);

    }

    // Endpoint for user login.
    @PostMapping(value = {"/login", "/login/"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntityResponseWithTokenDto> userLogin(@RequestBody @Valid LoginDto loginDto) {

        UserEntityResponseWithTokenDto responseDto = this.userService.userLogin(loginDto);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // Endpoint to find a user by ID.
    @GetMapping(value = {"/{userId}", "/{userId}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<UserEntityResponseDto> findUserById(@PathVariable("userId") Long userId) {
        // Get userEntity from db.
        UserEntity userEntity = this.userService.findUserEntityById(userId);

        // Map userEntity to userEntityResponseDto
        UserEntityResponseDto userEntityResponseDto = new UserEntityResponseDto();
        this.userMapper.mapUserEntityToUserEntityResponseDto(userEntity, userEntityResponseDto);

        return new ResponseEntity<>(userEntityResponseDto, HttpStatus.OK);
    }

    // Endpoint to update a user entity.
    @PutMapping(value = {"/{userId}", "/{userId}/"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<UserEntityResponseWithTokenDto> updateUserEntity(@PathVariable Long userId, @RequestBody @Valid UpdateUserEntityDto updateUserEntityDto) {

        // Map updateUserEntityDto to userEntityPartial
        UserEntity userEntityPartial = new UserEntity();
        this.userMapper.mapUpdateUserEntityDtoToUserEntity(updateUserEntityDto, userEntityPartial);

        // Save userEntity to db
        UserEntityResponseWithTokenDto responseWithTokenDto = userService.updateUserEntity(userId, userEntityPartial);

        // Return userEntityResponseDto with a status of 200
        return new ResponseEntity<>(responseWithTokenDto, HttpStatus.OK);

    }

    // Endpoint to delete a user by ID.
    @DeleteMapping(value = {"/{userId}", "/{userId}/"})
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {

        this.userService.deleteUserEntityByID(userId);

        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);

    }
}