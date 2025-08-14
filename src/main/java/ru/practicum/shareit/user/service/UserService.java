package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;

import java.util.List;

public interface UserService {
   UserDto getUser(long id);

   List<UserDto> getUsers();

   UserDto create(UserCreate userCreate);

   UserDto update(UserUpdate userUpdate);

   void delete(long id);
}
