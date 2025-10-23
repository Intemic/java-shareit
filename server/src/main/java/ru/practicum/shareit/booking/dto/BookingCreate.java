package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Data
public class BookingCreate {
    private long booker;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
