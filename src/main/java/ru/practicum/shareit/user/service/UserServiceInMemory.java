package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictResource;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;

import java.util.*;

@Service
public class UserServiceInMemory implements UserService {
    private static long id = 0L;
    Map<Long, User> users = new HashMap<>();

    private User getOneUser(long id) {
        User user = users.get(id);
        if (user == null)
            throw new NotFoundResource("Пользователь %d не найден".formatted(id));

        return user;
    }

    private Optional<User> getUserForEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();
    }

    private Optional<User> getUserForName(String name) {
        return users.values().stream()
                .filter(user -> user.getName().equals(name))
                .findAny();
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.mapToDto(getOneUser(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return users.values().stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    @Override
    public UserDto create(UserCreate userCreate) {
        User user = UserMapper.mapToUser(userCreate);
        if (getUserForName(userCreate.getName()).isPresent())
            throw new ConflictResource("Пользователь с name - %s уже присутствует".formatted(userCreate.getName()));

        if (getUserForEmail(userCreate.getEmail()).isPresent())
            throw new ConflictResource("Пользователь с email - %s уже присутствует".formatted(userCreate.getEmail()));

        user.setId(++id);
        users.put(user.getId(), user);
        return UserMapper.mapToDto(user);
    }

    @Override
    public UserDto update(UserUpdate userUpdate) {
        User oldUser = getOneUser(userUpdate.getId());
        oldUser = UserMapper.updateUser(userUpdate, oldUser);
        if (getUserForName(userUpdate.getName()).isPresent()
                && getUserForName(userUpdate.getName()).get().getId() != oldUser.getId())
            throw new ConflictResource("Пользователь с name - %s уже присутствует".formatted(userUpdate.getName()));

        if (getUserForEmail(userUpdate.getEmail()).isPresent()
                && getUserForEmail(userUpdate.getEmail()).get().getId() != oldUser.getId())
            throw new ConflictResource("Пользователь с email - %s уже присутствует".formatted(userUpdate.getEmail()));

        users.replace(userUpdate.getId(), oldUser);
        return UserMapper.mapToDto(oldUser);
    }

    @Override
    public void delete(long id) {
        getOneUser(id);
        users.remove(id);
    }
}
