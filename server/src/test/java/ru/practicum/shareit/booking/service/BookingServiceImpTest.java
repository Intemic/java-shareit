package ru.practicum.shareit.booking.service;

import com.sun.source.tree.LambdaExpressionTree;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStatusFilter;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ErrorParameter;
import ru.practicum.shareit.exception.ForbiddenResource;
import ru.practicum.shareit.exception.NotAvailable;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class BookingServiceImpTest {
    @MockBean
    private final BookingRepository bookingRepository;
    @MockBean
    private final UserService userService;
    @MockBean
    private final ItemService itemService;
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
                .start(LocalDateTime.now().plusHours(1))
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

    @Test
    public void testGetBookingForUser() {
        when(bookingRepository.findAllByBookerId(booker.getId())).thenReturn(List.of(booking));
        when(userService.getOneUser(booker.getId())).thenReturn(User.builder().build());

        List<BookingDto> listBookingDto = bookingService.getBooking(booker.getId(), BookingStatusFilter.ALL);
        assertThat(listBookingDto.size(), equalTo(1));
        assertThat(listBookingDto.getFirst().getId(), equalTo(booking.getId()));
        assertThat(listBookingDto.getFirst().getStart(), equalTo(booking.getStart()));
        assertThat(listBookingDto.getFirst().getEnd(), equalTo(booking.getEnd()));
        assertThat(listBookingDto.getFirst().getStatus(), equalTo(booking.getStatus()));
        assertThat(listBookingDto.getFirst().getItem().getId(), equalTo(booking.getItem().getId()));

        listBookingDto = bookingService.getBooking(booker.getId(), BookingStatusFilter.CURRENT);
        assertThat(listBookingDto.size(), equalTo(0));

        listBookingDto = bookingService.getBooking(booker.getId(), BookingStatusFilter.PAST);
        assertThat(listBookingDto.size(), equalTo(0));

        listBookingDto = bookingService.getBooking(booker.getId(), BookingStatusFilter.FUTURE);
        assertThat(listBookingDto.size(), equalTo(1));

        listBookingDto = bookingService.getBooking(booker.getId(), BookingStatusFilter.REJECTED);
        assertThat(listBookingDto.size(), equalTo(0));

        listBookingDto = bookingService.getBooking(booker.getId(), BookingStatusFilter.WAITING);
        assertThat(listBookingDto.size(), equalTo(1));
        assertThat(listBookingDto.getFirst().getId(), equalTo(booking.getId()));
        assertThat(listBookingDto.getFirst().getStart(), equalTo(booking.getStart()));
        assertThat(listBookingDto.getFirst().getEnd(), equalTo(booking.getEnd()));
        assertThat(listBookingDto.getFirst().getStatus(), equalTo(booking.getStatus()));
        assertThat(listBookingDto.getFirst().getItem().getId(), equalTo(booking.getItem().getId()));

        listBookingDto = bookingService.getBooking(5, BookingStatusFilter.ALL);
        assertThat(listBookingDto.size(), equalTo(0));

        Mockito.verify(bookingRepository, times(7)).findAllByBookerId(anyLong());
    }

    @Test
    public void testGetBookingOwner() {
        when(bookingRepository.findAllByItemOwnerId(owner.getId())).thenReturn(List.of(booking));
        when(userService.getOneUser(owner.getId())).thenReturn(User.builder().build());

        List<BookingDto> listBookingDto = bookingService.getBookingOwner(owner.getId(), BookingStatusFilter.ALL);

        assertThat(listBookingDto.size(), equalTo(1));
        assertThat(listBookingDto.getFirst().getId(), equalTo(booking.getId()));
        assertThat(listBookingDto.getFirst().getStart(), equalTo(booking.getStart()));
        assertThat(listBookingDto.getFirst().getEnd(), equalTo(booking.getEnd()));
        assertThat(listBookingDto.getFirst().getStatus(), equalTo(booking.getStatus()));
        assertThat(listBookingDto.getFirst().getItem().getId(), equalTo(booking.getItem().getId()));

        listBookingDto = bookingService.getBookingOwner(owner.getId(), BookingStatusFilter.CURRENT);
        assertThat(listBookingDto.size(), equalTo(0));

        listBookingDto = bookingService.getBookingOwner(owner.getId(), BookingStatusFilter.PAST);
        assertThat(listBookingDto.size(), equalTo(0));

        listBookingDto = bookingService.getBookingOwner(owner.getId(), BookingStatusFilter.FUTURE);
        assertThat(listBookingDto.size(), equalTo(1));

        listBookingDto = bookingService.getBookingOwner(owner.getId(), BookingStatusFilter.REJECTED);
        assertThat(listBookingDto.size(), equalTo(0));

        listBookingDto = bookingService.getBookingOwner(owner.getId(), BookingStatusFilter.WAITING);
        assertThat(listBookingDto.size(), equalTo(1));
        assertThat(listBookingDto.getFirst().getId(), equalTo(booking.getId()));
        assertThat(listBookingDto.getFirst().getStart(), equalTo(booking.getStart()));
        assertThat(listBookingDto.getFirst().getEnd(), equalTo(booking.getEnd()));
        assertThat(listBookingDto.getFirst().getStatus(), equalTo(booking.getStatus()));
        assertThat(listBookingDto.getFirst().getItem().getId(), equalTo(booking.getItem().getId()));

        listBookingDto = bookingService.getBookingOwner(5, BookingStatusFilter.ALL);
        assertThat(listBookingDto.size(), equalTo(0));

        Mockito.verify(bookingRepository, times(7)).findAllByItemOwnerId(anyLong());
    }

    @Test
    public void testMethodCreate() {
        BookingCreate bookingCreate = BookingCreate.builder()
                .booker(booker.getId())
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        Booking bookingNoId = BookingMapper.mapToBooking(bookingCreate, userService, itemService);
        BookingDto bookingDto = BookingMapper.mapToDto(booking);

        when(userService.getOneUser(booker.getId())).thenReturn(booker);
        when(itemService.getOneItem(item.getId())).thenReturn(item);
        when(bookingRepository.save(isA(Booking.class))).thenReturn(booking);

        BookingDto createBookingDto = bookingService.create(bookingCreate);
        assertThat(createBookingDto.getId(), equalTo(bookingDto.getId()));
        assertThat(createBookingDto.getStart(), equalTo(bookingDto.getStart()));
        assertThat(createBookingDto.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(createBookingDto.getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(createBookingDto.getBooker().getId(), equalTo(bookingDto.getBooker().getId()));
        assertThat(createBookingDto.getItem().getId(), equalTo(bookingDto.getItem().getId()));

        BookingCreate bookingCreate2 = bookingCreate.toBuilder()
                .start(LocalDateTime.now().minusMinutes(1))
                .build();
        ErrorParameter errorParameter = assertThrows(
                ErrorParameter.class,
                () -> bookingService.create(bookingCreate2));
        Assertions.assertEquals("Дата начала не может быть раньше текущей", errorParameter.getMessage());

        BookingCreate bookingCreate3 = bookingCreate.toBuilder()
                .end(LocalDateTime.now().minusDays(5))
                .build();
        errorParameter = assertThrows(
                ErrorParameter.class,
                () -> bookingService.create(bookingCreate3));
        Assertions.assertEquals("Дата окончания не может быть раньше текущей", errorParameter.getMessage());

        BookingCreate bookingCreate4 = bookingCreate.toBuilder()
                .start(bookingCreate.getStart())
                .end(bookingCreate.getStart())
                .build();

        BookingCreate bookingCreate5 = bookingCreate.toBuilder()
                .itemId(55L)
                .build();

        item.setAvailable(false);
        when(itemService.getOneItem(item.getId())).thenReturn(item);
        NotAvailable notAvailable = Assertions.assertThrows(
                NotAvailable.class,
                () -> bookingService.create(bookingCreate));
        Assertions.assertEquals("Операция невозможна, вещь не доступна", notAvailable.getMessage());
    }

    @Test
    public void testMethodChangeApproved() {
        Optional<Booking> optionalBooking = Optional.of(booking);
        when(bookingRepository.findById(booking.getId())).thenReturn(optionalBooking);

        when(bookingRepository.save(booking)).thenReturn(booking.toBuilder()
                .status(BookingStatus.APPROVED)
                .build());
        BookingDto bookingDto = bookingService.change_approved(booking.getId(), item.getOwner().getId(), "true");
        assertThat(bookingDto.getStatus(), equalTo(booking.getStatus()));

        when(bookingRepository.save(booking)).thenReturn(booking.toBuilder()
                .status(BookingStatus.REJECTED)
                .build());
        bookingDto = bookingService.change_approved(booking.getId(), item.getOwner().getId(), "false");
        assertThat(bookingDto.getStatus(), equalTo(booking.getStatus()));

        ForbiddenResource forbiddenResource = Assertions.assertThrows(
                ForbiddenResource.class,
                () -> bookingService.change_approved(booking.getId(), 5, "false"));
        Assertions.assertEquals("Недопустимая операция", forbiddenResource.getMessage());

    }
}