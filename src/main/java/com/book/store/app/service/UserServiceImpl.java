package com.book.store.app.service;

import com.book.store.app.dto.UserRegistrationRequestDto;
import com.book.store.app.dto.UserResponseDto;
import com.book.store.app.entity.Role;
import com.book.store.app.entity.RoleName;
import com.book.store.app.entity.User;
import com.book.store.app.exception.RegistrationException;
import com.book.store.app.mapper.UserMapper;
import com.book.store.app.repository.RoleRepository;
import com.book.store.app.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegistrationException("Email already in use");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RegistrationException("Default role USER not found"));
        user.setRoles(Set.of(userRole));

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
