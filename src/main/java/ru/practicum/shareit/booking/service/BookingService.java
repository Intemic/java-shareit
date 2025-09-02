package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {
    BookingDto create(BookingCreate booking);

    BookingDto change_approved(long bookingId, long userId, String approved);
}
