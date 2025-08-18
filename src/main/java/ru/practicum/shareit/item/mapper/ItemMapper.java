package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdate;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public static ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

    public static Item mapToItem(ItemCreate itemCreate) {
        return Item.builder()
                .owner(itemCreate.getOwner())
                .name(itemCreate.getName())
                .description(itemCreate.getDescription())
                .available(itemCreate.getAvailable())
                .build();
    }

    public static Item updateItem(ItemUpdate itemUpdate, Item item) {
        if (itemUpdate.hasName())
            item.setName(itemUpdate.getName());

        if (itemUpdate.hasDescription())
            item.setDescription(itemUpdate.getDescription());

        if (itemUpdate.hasAvailable())
            item.setAvailable(itemUpdate.getAvailable());

        return item;
    }
}
