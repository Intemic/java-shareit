package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Builder
@Data
public class ItemCreate {
    private User owner;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}

