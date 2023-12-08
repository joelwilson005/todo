package com.joel.todo.service;

import com.joel.todo.dto.LoginDto;
import com.joel.todo.dto.UserEntityResponseDto;
import com.joel.todo.dto.UserEntityResponseWithTokenDto;
import com.joel.todo.dto.UsernameAndEmailDto;
import com.joel.todo.exception.NotUniqueException;
import com.joel.todo.model.AccountStatus;
import com.joel.todo.model.Role;
import com.joel.todo.model.RoleType;
import com.joel.todo.model.UserEntity;
import com.joel.todo.repository.RoleRepository;
import com.joel.todo.repository.TodoListRepository;
import com.joel.todo.repository.UserRepository;
import com.joel.todo.util.UserMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final TodoListRepository todoListRepository;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTTokenService JWTTokenService;


    @Autowired
    public UserService(UserRepository userRepository, TodoListRepository todoListRepository, UserMapper userMapper, RoleRepository roleRepository, PasswordEncoder passwordEncoder, @Lazy AuthenticationManager authenticationManager, JWTTokenService JWTTokenService) {

        this.userRepository = userRepository;
        this.todoListRepository = todoListRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.JWTTokenService = JWTTokenService;

    }

    public void createUserEntity(UserEntity userEntity) throws NotUniqueException {

//       // Check to see if email and username are taken
        if (this.userRepository.findUserEntityByEmailAddress(userEntity.getEmailAddress()).isPresent() || this.userRepository.findEntityByUsername(userEntity.getUsername()).isPresent()) {

            throw new NotUniqueException("Email address and/or username must be unique.");

        } else {

            userEntity.setAccountStatus(AccountStatus.ACTIVE);
            this.setStatusDate(userEntity);
            Role userRole = roleRepository.findByAuthority(RoleType.USER.toString()).orElseThrow();
            Set<Role> authorities = new HashSet<>();
            authorities.add(userRole);
            userEntity.setAuthorities(authorities);
            userEntity.setPassword(this.passwordEncoder.encode(userEntity.getPassword()));


            this.userRepository.save(userEntity);

        }
    }

    public UserEntityResponseDto updateUserEntity(Long id, @Valid UserEntity userEntityPartial) {

        if (id.longValue() != userEntityPartial.getUserId().longValue()) {

            throw new IllegalArgumentException("Path variable and userEntity ID must match.");

        }
        // Capitalize first and last names. Trim whitespace as well
        userEntityPartial.setFirstName(StringUtils.trim(StringUtils.capitalize(userEntityPartial.getFirstName())));
        userEntityPartial.setLastName(StringUtils.trim(StringUtils.capitalize(userEntityPartial.getLastName())));

        // Obtain userEntity from db.
        UserEntity userEntityInDb = this.userRepository.findById(id).orElseThrow();

        // Map userEntityPartial to userEntityInDb
        this.userMapper.mapUserEntityPartialToUserEntity(userEntityPartial, userEntityInDb);

        // Save and return newly updated userEntity
        userEntityInDb = this.userRepository.save(userEntityInDb);


        // Map userEntity to userEntityResponseDto
        UserEntityResponseDto userEntityResponseDto = new UserEntityResponseDto();
        this.userMapper.mapUserEntityToUserEntityResponseDto(userEntityInDb, userEntityResponseDto);

        return userEntityResponseDto;

    }

    public UserEntity findUserEntityById(Long id) {

        return this.userRepository.findById(id).orElseThrow();
    }

    public void deleteUserEntityByID(Long id) {

        // Find UserEntity object
        UserEntity userEntity = this.userRepository.findById(id).orElseThrow();

        userEntity.setAccountStatus(AccountStatus.DELETED);
        this.setStatusDate(userEntity);

        this.userRepository.save(userEntity);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return this.userRepository.findEntityByUsername(username).orElseThrow();

    }


    public UserEntityResponseWithTokenDto userLogin(LoginDto loginDto) {

        // Find user based on username
        UserEntity userEntity = this.userRepository.findEntityByUsername(loginDto.getUsername()).orElseThrow();


        userEntity.setAccountStatus(AccountStatus.ACTIVE);

        userEntity = this.userRepository.save(userEntity);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        String token = JWTTokenService.generateJwt(auth, userEntity.getUserId());


        // Map userEntity to registrationAndLoginDto
        UserEntityResponseWithTokenDto userEntityResponseWithTokenDto = new UserEntityResponseWithTokenDto();
        this.userMapper.mapUserEntityToUserEntityResponseWithToken(userEntity, userEntityResponseWithTokenDto);

        // Add JWT token to userEntityResponseDto
        userEntityResponseWithTokenDto.setToken(token);

        return userEntityResponseWithTokenDto;

    }


    public void updatePasswordToken(String token, String email) throws NoSuchElementException {

        UserEntity userEntity = this.userRepository.findUserEntityByEmailAddress(email).orElseThrow();

        userEntity.setResetPasswordToken(token);

        this.userRepository.save(userEntity);

    }


    public UserEntity getByResetPasswordToken(String token) {

        return this.userRepository.findByResetPasswordToken(token).orElseThrow();
    }

    public void updatePassword(UserEntity userEntity, String newPassword) {

        String encodedPassword = this.passwordEncoder.encode(newPassword);

        userEntity.setPassword(encodedPassword);

        userEntity.setResetPasswordToken(null);

        this.userRepository.save(userEntity);
    }

    public void deleteExpiredUserEntities() {

        // Calculate the date 14 days ago
        LocalDate dateToDelete = LocalDate.now().minusDays(14);

        // Retrieve UserEntity records with account status set to DELETED and status set 14 days ago
        List<UserEntity> deletedUsers = userRepository.findByAccountStatusAndStatusSetDateBefore(AccountStatus.DELETED.toString(), dateToDelete);

        // Set child elements to null so that foreign key constraints are not violated.
        deletedUsers.forEach(
                userEntity -> {
                    userEntity.setAuthorities(null);
                    userEntity.setTodoLists(null);
                }
        );

        // Ensure that child todolists are deleted as well
        deletedUsers.forEach(userEntity -> this.todoListRepository.deleteAll(userEntity.getTodoLists()));

        // Delete the retrieved UserEntity records
        userRepository.deleteAll(deletedUsers);
    }

    public void setStatusDate(UserEntity userEntity) {

        userEntity.setStatusSetDate(LocalDateTime.now());

    }

    public Optional<UserEntity> findUserEntityByEmailAddress(String emailAddress) {

        return this.userRepository.findUserEntityByEmailAddress(emailAddress);

    }

    public Optional<UserEntity> findUserEntityByUsername(String username) {

        return this.userRepository.findEntityByUsername(username);

    }

    public HashMap<String, Boolean> isUsernameOrEmailAddressAlreadyPresent(UsernameAndEmailDto usernameAndEmailDto) {

        HashMap<String, Boolean> results = new HashMap<>();

        results.put("emailAddress", this.userRepository.findUserEntityByEmailAddress(usernameAndEmailDto.getEmailAddress()).isPresent());

        results.put("username", this.userRepository.findEntityByUsername(usernameAndEmailDto.getUsername()).isPresent());


        return results;
    }
}