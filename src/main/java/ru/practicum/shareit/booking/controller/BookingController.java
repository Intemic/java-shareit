package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

@Validated
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable("bookingId") Long bookingId) {
        return bookingService.getBooking(bookingId);
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
