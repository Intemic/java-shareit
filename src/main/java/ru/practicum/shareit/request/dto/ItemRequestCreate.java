package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequestCreate {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long owner;
    @NotBlank
    private String description;
}
