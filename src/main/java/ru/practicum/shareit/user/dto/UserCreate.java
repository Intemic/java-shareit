package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotBlank;

public class UserCreate {
    @NotBlank
    private String name;
    @NotBlank
    private String email;
}
