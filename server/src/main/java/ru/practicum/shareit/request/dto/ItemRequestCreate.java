package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ItemRequestCreate {
    private long owner;
    private String description;
}
