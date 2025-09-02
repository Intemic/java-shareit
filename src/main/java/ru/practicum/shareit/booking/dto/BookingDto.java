package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserDto booker;
    private ItemDto item;
}
