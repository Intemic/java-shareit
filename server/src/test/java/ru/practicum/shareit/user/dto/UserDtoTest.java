package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoTest {
    private final JacksonTester<UserDto> json;

    @Test
    public void testUserDto() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build();

        JsonContent<UserDto> result =  json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ivan@yandex.ru");
    }
}