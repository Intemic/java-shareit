package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utill.BaseStorage;

import java.util.List;

public interface ItemStorage extends BaseStorage<Item> {
    List<Item> getAll(long id);

    List<Item> search(String text);
}
