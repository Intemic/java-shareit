package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentCreate {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long itemId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long authorId;
    @NotBlank
    private String text;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime created = LocalDateTime.now();
}
