package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictResource;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserStorage userStorage;

    public User getOneUser(long id) {
        Optional<User> userOptional = userStorage.get(id);
        if (userOptional.isEmpty())
            throw new NotFoundResource("Пользователь %d не найден".formatted(id));

        return userOptional.get();
    }

    private void checkData(User user) {
        Long userId = null;
        Optional<User> optionalUser = userStorage.getUserForName(user.getName());
        if (optionalUser.isPresent())
            if (user.getId() == null || !user.equals(optionalUser.get()))
                throw new ConflictResource("Пользователь с name - %s уже присутствует".formatted(user.getName()));


        optionalUser = userStorage.getUserForEmail(user.getEmail());
        if (optionalUser.isPresent())
            if (user.getId() == null || !user.equals(optionalUser.get()))
                throw new ConflictResource("Пользователь с email - %s уже присутствует".formatted(user.getEmail()));
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.mapToDto(getOneUser(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return userStorage.getAll().stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    @Override
    public UserDto create(UserCreate userCreate) {
        User user = UserMapper.mapToUser(userCreate);
        checkData(user);
        return UserMapper.mapToDto(userStorage.create(user));
    }

    @Override
    public UserDto update(UserUpdate userUpdate) {
        User oldUser = getOneUser(userUpdate.getId());
        User updateUser = UserMapper.updateUser(userUpdate, oldUser);
        checkData(updateUser);
        return UserMapper.mapToDto(userStorage.update(updateUser));
    }

    @Override
    public void delete(long id) {
        getOneUser(id);
        userStorage.delete(id);
    }
}
