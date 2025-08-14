package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(of = {"id"})
public class Item {
    private long id;
    private long owner;
    private String name;
    private String description;
    private boolean available;
}
