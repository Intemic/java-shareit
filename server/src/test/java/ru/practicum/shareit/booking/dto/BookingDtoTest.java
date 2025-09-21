package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoTest {
    private final JacksonTester<BookingDto> json;

    @Test
    public void testDto() throws IOException {
        UserDto bookerDto = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build();
        UserDto ownerDto = UserDto.builder()
                .id(2L)
                .name("Elena")
                .email("elena@yandex.ru")
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(3L)
                .name("Утюг")
                .description("Что то гладить")
                .available(true)
                .owner(ownerDto)
                .build();
        BookingDto bookingDto = BookingDto.builder()
                .id(4L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.WAITING)
                .booker(bookerDto)
                .item(itemDto)
                .build();

        JsonContent<BookingDto> result =  json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().toString());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(3);
    }

}