package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoTest {
    private final JacksonTester<ItemDto> json;

    @Test
    public void testItemDto() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Ilya")
                .email("ilya@yandex.ru")
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Какой то комментарий")
                .authorName("Юлия")
                .created(LocalDateTime.now())
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Пылесос")
                .description("Очищает квартиру")
                .available(true)
                .owner(userDto)
                .comments(List.of(commentDto))
                .build();

        JsonContent<ItemDto> result =  json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.isAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.owner.id")
                .isEqualTo(1);
    }
}