package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Valid
@Builder
@Data
public class BookingCreate {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long booker;
    @NotNull
    private Long itemId;
    @NotNull
    @DateTimeFormat(style = "yyyy-MM-dd:mm:ss")
    private LocalDateTime start;
    @NotNull
    @DateTimeFormat(style = "yyyy-MM-dd:mm:ss")
    private LocalDateTime end;
}
