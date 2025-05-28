package com.book.store.app.service;

import com.book.store.app.dto.UserLoginRequestDto;
import com.book.store.app.dto.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto login(UserLoginRequestDto request);
}
