package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Builder
@Data
@EqualsAndHashCode(of = {"id"})
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private UserDto owner;
    private List<CommentDto> comments;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
}
