package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    public ItemRequest getRequestOne(long requestId) {
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
        if (itemRequestOptional.isEmpty())
            throw new NotFoundResource("Запрос %d не найден".formatted(requestId));

        return itemRequestOptional.get();
    }

    @Override
    public ItemRequestDto getRequest(long requestId) {
        return ItemRequestMapper.mapToDto(getRequestOne(requestId));
    }

    @Override
    public List<ItemRequestDto> getYourRequests(long userId) {
        return itemRequestRepository.findAllByOwnerIdOrderByCreated(userId).stream()
                .map(ItemRequestMapper::mapToDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getRequestsExcludingYour(long excludingUserId) {
        return itemRequestRepository.findAllByOwnerIdNotOrderByCreated(excludingUserId).stream()
                .map(ItemRequestMapper::mapToDto)
                .toList();
    }

    @Override
    public ItemRequestDto createRequest(ItemRequestCreate itemRequestCreate) {
        return ItemRequestMapper.mapToDto(itemRequestRepository.save(ItemRequestMapper
                        .mapToItemRequest(itemRequestCreate, userService)));
    }
}
