package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(of = {"id"})
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
}
