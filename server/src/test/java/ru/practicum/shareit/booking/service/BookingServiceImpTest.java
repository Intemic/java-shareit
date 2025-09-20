package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenResource;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class BookingServiceImpTest {
    @MockBean
    private final BookingRepository bookingRepository;
    @MockBean
    private final UserService userService;
    private final BookingService bookingService;
    private Booking booking;
    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    public void before() {
        booker = User.builder()
                .id(1L)
                .name("Igor")
                .email("igor@yandex.ru")
                .build();
        owner = User.builder()
                .id(2L)
                .name("Elena")
                .email("elena@yandex.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("Утюг")
                .description("Гладить вещи")
                .available(true)
                .build();
        booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    public void testGetBooking() {
        Optional<Booking> optionalBooking = Optional.of(booking);
        when(bookingRepository.findById(booking.getId())).thenReturn(optionalBooking);
        when(userService.getOneUser(booker.getId())).thenReturn(User.builder().build());

       BookingDto bookingDto = bookingService.getBooking(booking.getId(), booker.getId());

        assertThat(bookingDto.getId(), equalTo(booking.getId()));
        assertThat(bookingDto.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(bookingDto.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(bookingDto.getStart(), equalTo(booking.getStart()));
        assertThat(bookingDto.getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDto.getStatus(), equalTo(booking.getStatus()));

        ForbiddenResource exception = Assertions.assertThrows(
                ForbiddenResource.class,
                () -> bookingService.getBooking(booking.getId(), 5));
        Assertions.assertEquals("Отсутствуют полномочия на операцию", exception.getMessage());

        booking.setId(5);
        when(bookingRepository.findById(booking.getId()))
                .thenThrow(new NotFoundResource("Не найдена бронь %d".formatted(booking.getId())));

        NotFoundResource exceptionNotFound = Assertions.assertThrows(
                NotFoundResource.class,
                () -> bookingService.getBooking(booking.getId(), booker.getId()));
        Assertions.assertEquals("Не найдена бронь %d".formatted(booking.getId()),
                exceptionNotFound.getMessage());

        when(userService.getOneUser(booker.getId()))
                .thenThrow(new NotFoundResource("Пользователь %d не найден".formatted(booker.getId())));
        exceptionNotFound = Assertions.assertThrows(
                NotFoundResource.class,
                () -> bookingService.getBooking(booking.getId(), booker.getId()));
        Assertions.assertEquals("Пользователь %d не найден".formatted(booker.getId()),
                exceptionNotFound.getMessage());


    }

}