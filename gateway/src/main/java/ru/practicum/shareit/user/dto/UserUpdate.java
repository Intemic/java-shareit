package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdate {
    private String name;
    @Email(message = "Некорректный email")
    private String email;
}
