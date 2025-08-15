package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenResource;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    private Item getOneItem(Long id) {
        Optional<Item> optionalItem = itemStorage.get(id);
        if (optionalItem.isEmpty())
            throw new NotFoundResource("Не найдена вещь %d".formatted(id));

        return optionalItem.get();
    }

    @Override
    public ItemDto getItem(Long id) {
        return ItemMapper.mapToDto(getOneItem(id));
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemStorage.getAll(userId).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemStorage.search(text).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public ItemDto create(ItemCreate item) {
        userService.getOneUser(item.getOwner());

        return ItemMapper.mapToDto(itemStorage.create(ItemMapper.mapToItem(item)));
    }

    @Override
    public ItemDto update(ItemUpdate item, long userId) {
        Item oldItem = getOneItem(item.getId());
        if (oldItem.getOwner() != userId)
            throw new ForbiddenResource("Отсутсвую полномочия на операцию");

        Item updateItem = ItemMapper.updateItem(item, oldItem);
        itemStorage.update(updateItem);
        return ItemMapper.mapToDto(updateItem);
    }
}
