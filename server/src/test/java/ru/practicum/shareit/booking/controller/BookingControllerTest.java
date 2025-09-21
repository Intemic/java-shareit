package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStatusFilter;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ErrorParameter;
import ru.practicum.shareit.exception.ForbiddenResource;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private Booking booking;
    private User booker;
    private User owner;
    private Item item;
    private BookingDto bookingDto;

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
                .id(4L)
                .owner(owner)
                .name("Утюг")
                .description("Гладить вещи")
                .available(true)
                .build();
        booking = Booking.builder()
                .id(5L)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = BookingMapper.mapToDto(booking);
    }

    @Test
    public void testMethodGetBooking() throws Exception {
        when(bookingService.getBooking(booking.getId(), booker.getId())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/" + booking.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", equalTo(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", equalTo(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", equalTo(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", equalTo(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", equalTo(booking.getItem().getId()), Long.class)
                );

        when(bookingService.getBooking(booking.getId(), 55))
                .thenThrow(new ForbiddenResource("Отсутствуют полномочия на операцию"));

        mvc.perform(get("/bookings/" + booking.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 55))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", equalTo("Отсутствуют полномочия на операцию"))
                );
    }

    @Test
    public void testGetBookings() throws Exception {
        when(bookingService.getBooking(booker.getId(), BookingStatusFilter.ALL)).thenReturn(List.of(bookingDto));
        when(bookingService.getBooking(booker.getId(), BookingStatusFilter.FUTURE)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", equalTo(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", equalTo(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", equalTo(booking.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", equalTo(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", equalTo(booking.getItem().getId()), Long.class)
                );

        //when(bookingService.getBooking(booker.getId(), BookingStatusFilter.CURRENT)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0))
                );

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "PAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0))
                );

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", equalTo(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", equalTo(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", equalTo(booking.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", equalTo(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", equalTo(booking.getItem().getId()), Long.class)
                );

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0))
                );

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0))
                );
    }

    @Test
    public void testMethodGetBookingsForOwner() throws Exception {
        when(bookingService.getBookingOwner(owner.getId(), BookingStatusFilter.ALL)).thenReturn(List.of(bookingDto));
        when(bookingService.getBookingOwner(owner.getId(), BookingStatusFilter.FUTURE)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", equalTo(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", equalTo(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", equalTo(booking.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", equalTo(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", equalTo(booking.getItem().getId()), Long.class)
                );

        //when(bookingService.getBooking(booker.getId(), BookingStatusFilter.CURRENT)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0))
                );

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "PAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0))
                );

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", equalTo(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", equalTo(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", equalTo(booking.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", equalTo(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", equalTo(booking.getItem().getId()), Long.class)
                );

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0))
                );

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0))
                );
    }

    @Test
    public void testMethodCreate() throws Exception {
        BookingCreate bookingCreate = BookingCreate.builder()
                .booker(booker.getId())
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        when(bookingService.create(bookingCreate)).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(mapper.writeValueAsString(bookingCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", equalTo(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", equalTo(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", equalTo(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", equalTo(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", equalTo(booking.getItem().getId()), Long.class)
                );

        BookingCreate bookingCreate2 = bookingCreate.toBuilder()
                .start(booking.getStart().minusDays(1))
                .build();
        when(bookingService.create(bookingCreate2))
                .thenThrow(new ErrorParameter("Дата начала не может быть раньше текущей"));
        mvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(mapper.writeValueAsString(bookingCreate2)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", equalTo("Дата начала не может быть раньше текущей"))
                );

        BookingCreate bookingCreate3 = bookingCreate.toBuilder()
                .end(LocalDateTime.now().minusDays(1))
                .build();
        when(bookingService.create(bookingCreate3))
                .thenThrow(new ErrorParameter("Дата окончания не может быть раньше текущей"));
        mvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(mapper.writeValueAsString(bookingCreate3)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", equalTo("Дата окончания не может быть раньше текущей"))
                );
    }

    @Test
    public void testMethodChangeApproved() throws Exception {
        BookingDto bookingDtoApproved = bookingDto.toBuilder()
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.change_approved(booking.getId(), owner.getId(), "true"))
                .thenReturn(bookingDtoApproved);

        mvc.perform(patch("/bookings/" + booking.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoApproved.getId()), Long.class))
                .andExpect(jsonPath("$.start", equalTo(bookingDtoApproved.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", equalTo(bookingDtoApproved.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", equalTo(bookingDtoApproved.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", equalTo(bookingDtoApproved.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", equalTo(bookingDtoApproved.getItem().getId()), Long.class)
                );

        BookingDto bookingDtoRejected = bookingDto.toBuilder()
                .status(BookingStatus.REJECTED)
                .build();
        when(bookingService.change_approved(booking.getId(), owner.getId(), "false"))
                .thenReturn(bookingDtoRejected);

        mvc.perform(patch("/bookings/" + booking.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoRejected.getId()), Long.class))
                .andExpect(jsonPath("$.start", equalTo(bookingDtoRejected.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", equalTo(bookingDtoRejected.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", equalTo(bookingDtoRejected.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", equalTo(bookingDtoRejected.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", equalTo(bookingDtoRejected.getItem().getId()), Long.class)
                );

    }
}