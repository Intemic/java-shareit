package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemCreate {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Boolean available;
}

