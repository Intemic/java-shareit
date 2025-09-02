package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.model.User;

@Builder
@Data
@EqualsAndHashCode(of = {"id"})
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
}
