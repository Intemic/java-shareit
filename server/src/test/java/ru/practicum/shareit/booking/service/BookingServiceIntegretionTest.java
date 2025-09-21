package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.properties"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegretionTest {
    private final BookingService bookingService;
    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    public void testCreate() {
        UserCreate bookerCreate = UserCreate.builder()
                .name("Igor")
                .email("igor@yandex.ru")
                .build();
        UserCreate ownerCreate = UserCreate.builder()
                .name("Elena")
                .email("elena@yandex.ru")
                .build();
        User booker = UserMapper.mapToUserFromDto(userService.create(bookerCreate));
        User owner = UserMapper.mapToUserFromDto(userService.create(ownerCreate));

        ItemCreate itemCreate = ItemCreate.builder()
                .name("Утюг")
                .description("Гладить вещи")
                .available(true)
                .owner(owner)
                .build();
        ItemDto itemDto = itemService.create(itemCreate, owner.getId());

        BookingCreate bookingCreate = BookingCreate.builder()
                .booker(booker.getId())
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto bookingDto = bookingService.create(bookingCreate);

        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.booker.id = :booker", Booking.class);
        Booking bookingCreated = query.setParameter("booker", booker.getId())
                .getSingleResult();

        assertThat(bookingCreated.getId(), notNullValue());
        assertThat(bookingCreated.getStart(), equalTo(bookingCreate.getStart()));
        assertThat(bookingCreated.getEnd(), equalTo(bookingCreate.getEnd()));
    }
}
