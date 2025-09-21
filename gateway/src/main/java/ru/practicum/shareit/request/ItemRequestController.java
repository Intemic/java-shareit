package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreate;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private final static String USER_HEAD = "X-Sharer-User-Id";

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable long requestId) {
        log.info("Get ItemRequest with id - %d".formatted(requestId));
        return itemRequestClient.getRequest(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getYourRequests(@RequestHeader(USER_HEAD) @Positive long userId) {
        log.info("Get all ItemRequest for user - %d".formatted(userId));
        return itemRequestClient.getYourRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsExcludingYour(
            @RequestHeader(USER_HEAD) long excludingUserId) {
        log.info("Get all ItemRequest excluding user - %d".formatted(excludingUserId));
        return itemRequestClient.getRequestsExcludingYour(excludingUserId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody @Valid ItemRequestCreate item,
                                                @RequestHeader(USER_HEAD) @Positive Long userId) {
        log.info("Create ItemRequest with data - %s".formatted(item));
        return itemRequestClient.createRequest(item, userId);
    }
}
