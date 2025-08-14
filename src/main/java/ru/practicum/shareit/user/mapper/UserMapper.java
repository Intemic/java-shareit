package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;

public class UserMapper {
    public static UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapToUser(UserCreate userCreate) {
        return User.builder()
                .name(userCreate.getName())
                .email(userCreate.getEmail())
                .build();
    }

    public static User updateUser(UserUpdate userUpdate, User user) {
        if (userUpdate.hasName())
            user.setName(userUpdate.getName());

        if (userUpdate.hasEmail())
            user.setEmail(userUpdate.getEmail());

        return user;
    }

}
