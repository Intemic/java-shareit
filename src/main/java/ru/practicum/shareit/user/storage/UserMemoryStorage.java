package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserMemoryStorage implements UserStorage {
    private static long id = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> getUserForName(String name) {
        return users.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst();
    }

    @Override
    public Optional<User> getUserForEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User create(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        return users.replace(user.getId(), user);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return users.values().stream()
                .toList();
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
