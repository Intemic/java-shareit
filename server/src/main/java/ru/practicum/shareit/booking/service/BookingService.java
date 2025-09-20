package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingStatusFilter;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto getBooking(long id, long userId);

    BookingDto create(BookingCreate booking);

    BookingDto change_approved(long bookingId, long userId, String approved);

    List<BookingDto> getBooking(long userId, BookingStatusFilter statusFilter);

    List<BookingDto> getBookingOwner(long userId, BookingStatusFilter statusFilter);
}
