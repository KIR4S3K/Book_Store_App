package com.book.store.app.controller;

import com.book.store.app.dto.UserLoginRequestDto;
import com.book.store.app.dto.UserLoginResponseDto;
import com.book.store.app.dto.UserRegistrationRequestDto;
import com.book.store.app.dto.UserResponseDto;
import com.book.store.app.service.AuthenticationService;
import com.book.store.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserResponseDto register(
            @Valid @RequestBody UserRegistrationRequestDto request
    ) {
        return userService.register(request);
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public UserLoginResponseDto login(
            @Valid @RequestBody UserLoginRequestDto request
    ) {
        return authenticationService.login(request);
    }
}
