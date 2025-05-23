package com.book.store.app.mapper;

import com.book.store.app.dto.UserRegistrationRequestDto;
import com.book.store.app.dto.UserResponseDto;
import com.book.store.app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserRegistrationRequestDto dto);

    UserResponseDto toDto(User user);
}
