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

import java.util.HashMap;

// Controller class for handling user-related operations.
@CrossOrigin(origins = {"https://silly-alfajores-7fb4bf.netlify.app/"}, allowCredentials = "true")
@RestController
@RequestMapping({"/users", "/users/"})
public class UserController {

    // Service for handling user operations
    private final UserService userService;
    // Mapper for converting between DTOs and Entities
    private final UserMapper userMapper;

    // Constructor injection of UserService and UserMapper
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

        // Prepare loginDto for login after registration
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(createUserEntityDto.getUsername());
        loginDto.setPassword(password);

        // Call the userLogin method within the same controller
        return userLogin(loginDto);
    }

    // Endpoint for user login.
    @PostMapping(value = {"/login", "/login/"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntityResponseWithTokenDto> userLogin(@RequestBody @Valid LoginDto loginDto) {
        // Call userService to login user and get response
        UserEntityResponseWithTokenDto responseDto = this.userService.userLogin(loginDto);

        // Return response with status OK
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

        // Return response with status OK
        return new ResponseEntity<>(userEntityResponseDto, HttpStatus.OK);
    }

    // Endpoint to update a user entity.
    @PutMapping(value = {"/{userId}", "/{userId}/"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<UserEntityResponseDto> updateUserEntity(@PathVariable Long userId, @RequestBody @Valid UpdateUserEntityDto updateUserEntityDto) {
        // Map updateUserEntityDto to userEntityPartial
        UserEntity userEntityPartial = new UserEntity();
        this.userMapper.mapUpdateUserEntityDtoToUserEntity(updateUserEntityDto, userEntityPartial);

        // Update userEntity in db
        UserEntityResponseDto userEntityResponseDto = userService.updateUserEntity(userId, userEntityPartial);

        // Return updated userEntityResponseDto with a status of 200
        return new ResponseEntity<>(userEntityResponseDto, HttpStatus.OK);
    }

    // Endpoint to delete a user by ID.
    @DeleteMapping(value = {"/{userId}", "/{userId}/"})
    @PreAuthorize("authentication.principal.claims['userId'] == #userId and authentication.principal.claims['roles'] == 'USER'")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        // Delete userEntity by ID
        this.userService.deleteUserEntityByID(userId);

        // Return success message with status OK
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

    // Endpoint to check if username and email are unique
    @PostMapping(value = "/checkusernameandemail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkUsernameAndEmailUniqueness(@Valid @RequestBody UsernameAndEmailDto usernameAndEmailDto) {
        // Check if username or email is already present
        HashMap<String, Boolean> results = this.userService.isUsernameOrEmailAddressAlreadyPresent(usernameAndEmailDto);

        // Return results with status OK
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}