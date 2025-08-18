package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdate;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable(name = "itemId") @Positive Long id) {
        return itemService.getItem(id);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto create(@RequestBody @Valid ItemCreate item,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        item.setOwner(userId);
        return itemService.create(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody @Valid ItemUpdate item,
                          @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        item.setId(itemId);
        return itemService.update(item, userId);
    }
}
