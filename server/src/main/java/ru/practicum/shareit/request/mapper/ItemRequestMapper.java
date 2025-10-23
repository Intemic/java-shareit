package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() != null ? itemRequest.getItems().stream()
                        .map(ItemMapper::mapToDto)
                        .toList() : null)
                .build();
    }

    public static ItemRequest mapToItemRequest(ItemRequestCreate itemRequestCreate,
                                               UserService userService) {
        return ItemRequest.builder()
                .owner(userService.getOneUser(itemRequestCreate.getOwner()))
                .created(LocalDateTime.now())
                .description(itemRequestCreate.getDescription())
                .build();
    }
}
