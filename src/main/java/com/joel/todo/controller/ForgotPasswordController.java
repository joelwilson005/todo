package com.joel.todo.controller;

import com.joel.todo.dto.LoginDto;
import com.joel.todo.dto.UserEntityResponseWithTokenDto;
import com.joel.todo.exception.InvalidTokenException;
import com.joel.todo.model.UserEntity;
import com.joel.todo.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;

@RestController
public class ForgotPasswordController {

    private final JavaMailSender mailSender;
    private final UserService userService;

    @Autowired
    public ForgotPasswordController(JavaMailSender mailSender, UserService userService) {
        this.mailSender = mailSender;
        this.userService = userService;
    }

    // Inner class representing the email address in the request
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Email {

        @jakarta.validation.constraints.Email
        private String emailAddress;

    }

    // Inner class representing email, verification code, and password in the request
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class EmailAndTokenDto {

        @NotEmpty
        @jakarta.validation.constraints.Email
        private String emailAddress;

        @NotNull
        private Integer verificationCode;

    /*
        Should have:
        1 lowercase letter,
        1 uppercase letter,
        1 number,
        1 special character
        and be at least 8 characters long
     */
        @Pattern(regexp = "(?=(.*[0-9]))((?=.*[A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z]))^.{8,}$", message = "Invalid password")
        String password;

    }

    // Endpoint to process forgotten passwords
    @PostMapping(value = {"/forgot", "/forgot/"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processForgottenPassword(HttpServletRequest request, @Valid @RequestBody Email email) {

        try {

            if (this.userService.findUserEntityByEmailAddress(email.getEmailAddress()).isPresent()) {

                // Generate a random verification token
                long currentTimeMillis = System.currentTimeMillis();
                Integer token = (int) (currentTimeMillis % 1000000);


                // Update the user's password token in the database
                this.userService.updatePasswordToken(token, email.getEmailAddress());

                // Set when the token was added to the db. Tokens are only valid for 5 mins


                // Send the verification token via email
                sendEmail(email.getEmailAddress(), token);

                // Return a success response
                return ResponseEntity.ok().build();

            } else {

                throw new NoSuchElementException();
            }


        } catch (NoSuchElementException e) {

            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        } catch (MessagingException | UnsupportedEncodingException e) {

            // Handle exceptions and return a bad request response in case of errors
            return new ResponseEntity<>("An error has occurred", HttpStatus.BAD_REQUEST);

        }
    }

    // Method to send a verification token via email
    public void sendEmail(String recipientEmail, Integer token) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("todoapplication876@gmail.com", "TodoApplication Support");
        helper.setTo(recipientEmail);

        // Email content including the verification token
        String subject = "Here's the verification code to reset your password";
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p><b>Verification Code: </b></p>"
                + "<h2>" + token + "</h2>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        // Set email subject and content
        helper.setSubject(subject);
        helper.setText(content, true);

        // Send the email
        mailSender.send(message);

    }

    // Endpoint to process password reset
    @PostMapping(value = {"/reset_password", "/reset_password/"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processResetPassword(@RequestBody @Valid EmailAndTokenDto emailAndTokenDto) {

        // Retrieve user entity by verification token
        UserEntity userEntity = this.userService.getByResetPasswordToken(emailAndTokenDto.getVerificationCode());

        final String password = emailAndTokenDto.getPassword();

        // Handle invalid verification token
        if (userEntity == null) {

            try {

                throw new InvalidTokenException("Invalid token");

            } catch (InvalidTokenException e) {

                // Return a bad request response with an error message
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

            }
        } else {

            // Update user's password, login, and return user details with a token
            this.userService.updatePassword(userEntity, emailAndTokenDto.getPassword());

            LoginDto loginDto = new LoginDto();
            loginDto.setUsername(userEntity.getUsername());
            loginDto.setPassword(password);

            UserEntityResponseWithTokenDto responseDto = this.userService.userLogin(loginDto);

            // Return the user details with OK status
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        }
    }
}