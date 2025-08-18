package ru.practicum.shareit.utill;

import java.util.List;
import java.util.Optional;

public interface BaseStorage<T> {
    T create(T object);

    T update(T object);

    void delete(Long id);

    List<T> getAll();

    Optional<T> get(Long id);
}