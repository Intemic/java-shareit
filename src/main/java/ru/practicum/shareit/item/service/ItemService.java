package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getOneItem(Long id);

    ItemDto getItem(Long id);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> search(String text);

    ItemDto create(ItemCreate item, long userId);

    ItemDto update(ItemUpdate item, long userId);

    CommentDto createComment(CommentCreate commentCreate);
}
