package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;

@Validated
@RestController
@RequestMapping("/bookings")
public class BookingController {
    @PostMapping
    public BookingDto create(@RequestBody @Valid BookingCreate booking) {
        return null;
    }

// ???????????????
//    @PatchMapping("/{bookingId}")
//    public

}
