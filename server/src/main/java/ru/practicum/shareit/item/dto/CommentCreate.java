package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Data
public class CommentCreate {
    private long itemId;
    private long authorId;
    private String text;
    private LocalDateTime created = LocalDateTime.now();
}
