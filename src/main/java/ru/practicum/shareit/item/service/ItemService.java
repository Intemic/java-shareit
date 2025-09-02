package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdate;

import java.util.List;

public interface ItemService {
    ItemDto getItem(Long id);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> search(String text);

    ItemDto create(ItemCreate item, long userId);

    ItemDto update(ItemUpdate item, long userId);
}
