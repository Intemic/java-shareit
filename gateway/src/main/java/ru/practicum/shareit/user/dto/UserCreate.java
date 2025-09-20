package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UserCreate {
    @NotBlank
    private String name;
    @NotBlank
    @Email(message = "Некорректный email")
    private String email;
}
