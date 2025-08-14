package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utill.BaseStorage;

import java.util.Optional;

public interface UserStorage extends BaseStorage<User> {
    Optional<User> getUserForName(String name);

    Optional<User> getUserForEmail(String email);
}
