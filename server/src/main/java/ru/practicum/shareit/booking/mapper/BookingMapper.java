package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

@UtilityClass
public class BookingMapper {
    public static BookingDto mapToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(UserMapper.mapToDto(booking.getBooker()))
                .item(ItemMapper.mapToDto(booking.getItem()))
                .build();
    }

    public static Booking mapToBooking(BookingCreate bookingCreate,
                                       UserService userService,
                                       ItemService itemService) {
        return Booking.builder()
                .booker(userService.getOneUser(bookingCreate.getBooker()))
                .item(itemService.getOneItem(bookingCreate.getItemId()))
                .start(bookingCreate.getStart())
                .end(bookingCreate.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }
}
