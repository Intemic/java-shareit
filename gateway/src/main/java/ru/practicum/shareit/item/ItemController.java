package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreate;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemUpdate;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final static String USER_HEAD = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable(name = "itemId") @Positive Long itemId) {
        log.info("Get item with id - %d".formatted(itemId));
        return itemClient.getItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_HEAD) @Positive Long userId) {
        log.info("Get items for user - %d".formatted(userId));
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Search item for text - %s".formatted(text));
        return itemClient.search(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemCreate item,
                                         @RequestHeader(USER_HEAD) @Positive Long userId) {
        log.info("Create item - %s".formatted(item));
        return itemClient.create(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentCreate comment,
                                                @PathVariable Long itemId,
                                                @RequestHeader(USER_HEAD) @Positive Long userId) {
        log.info("Create comment for item - %d, data - %s".formatted(itemId, comment));
        return itemClient.createComment(comment, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody @Valid ItemUpdate item,
                                         @PathVariable Long itemId,
                                         @RequestHeader(USER_HEAD) @Positive Long userId) {
        log.info("Update item - %d, data - %s".formatted(itemId, item));
        return itemClient.update(item, itemId, userId);
    }
}
