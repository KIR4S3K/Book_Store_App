package com.book.store.app.service;

import com.book.store.app.dto.UserRegistrationRequestDto;
import com.book.store.app.dto.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request);
}
