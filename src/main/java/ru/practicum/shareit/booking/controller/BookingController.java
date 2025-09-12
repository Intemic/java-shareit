package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingStatusFilter;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable("bookingId") Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(defaultValue = "ALL") String state,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingStatusFilter statusFilter;

        try {
            statusFilter = BookingStatusFilter.valueOf(state);
        } catch (IllegalArgumentException ex) {
            statusFilter = BookingStatusFilter.ALL;
        }
        return bookingService.getBooking(userId, statusFilter);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingStatusFilter statusFilter;

        try {
            statusFilter = BookingStatusFilter.valueOf(state);
        } catch (IllegalArgumentException ex) {
            statusFilter = BookingStatusFilter.ALL;
        }
        return bookingService.getBookingOwner(userId, statusFilter);
    }

    @PostMapping
    public BookingDto create(@RequestBody @Valid BookingCreate booking,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        booking.setBooker(userId);
        return bookingService.create(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto change_approved(@PathVariable Long bookingId,
                                      @RequestParam String approved,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.change_approved(bookingId, userId, approved);
    }

}
