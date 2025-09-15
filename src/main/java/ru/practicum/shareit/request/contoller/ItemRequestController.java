package ru.practicum.shareit.request.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;


    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable long requestId) {
        return itemRequestService.getRequest(requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getYourRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getYourRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsExcludingYour(
            @RequestHeader("X-Sharer-User-Id") long excludingUserId) {
        return itemRequestService.getRequestsExcludingYour(excludingUserId);
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody @Valid ItemRequestCreate item,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        item.setOwner(userId);
        return itemRequestService.createRequest(item);
    }
}
