package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest getRequestOne(long requestId);

    ItemRequestDto getRequest(long requestId);

    List<ItemRequestDto> getYourRequests(long userId);

    List<ItemRequestDto> getRequestsExcludingYour(long excludingUserId);

    ItemRequestDto createRequest(ItemRequestCreate itemRequestCreate);
}
