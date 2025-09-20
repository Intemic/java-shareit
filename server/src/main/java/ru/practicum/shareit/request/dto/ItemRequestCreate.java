package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequestCreate {
    private long owner;
    private String description;
}
