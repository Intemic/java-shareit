package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictResource;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    public void before() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Igor")
                .email("igor@yandex.ru")
                .build();
    }

    @Test
    public void testMethodGetUser() throws Exception {

        when(userService.getUser(1L)).thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        when(userService.getUser(5L))
                .thenThrow(new NotFoundResource("Пользователь 5 не найден"));

        mvc.perform(get("/users/5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", equalTo("Пользователь 5 не найден")));

    }

    @Test
    public void testMethodGetUsers() throws Exception {
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("elena")
                .email("elena@yandex.ru")
                .build();
        when(userService.getUsers()).thenReturn(List.of(userDto, userDto2));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(2)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));
    }

    @Test
    public void testMethodCreate() throws Exception {
        UserCreate userCreate = UserCreate.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
        when(userService.create(userCreate)).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        when(userService.create(userCreate))
                .thenThrow(new ConflictResource("Пользователь с email - %s уже присутствует"
                        .formatted(userCreate.getEmail())));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", equalTo("Пользователь с email - %s уже присутствует"
                        .formatted(userCreate.getEmail()))));
    }

    @Test
    public void testMethodUpdate() throws Exception {
        UserUpdate userUpdate = UserUpdate.builder()
                .id(userDto.getId())
                .name("Oleg")
                .email("oleg@yandex.ru")
                .build();
        userDto = userDto.toBuilder()
                .name(userUpdate.getName())
                .email(userUpdate.getEmail())
                .build();

        when(userService.update(userUpdate)).thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        when(userService.update(userUpdate))
                .thenThrow(new ConflictResource("Пользователь с email - %s уже присутствует"
                        .formatted(userUpdate.getEmail())));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", equalTo("Пользователь с email - %s уже присутствует"
                        .formatted(userUpdate.getEmail()))));
    }

    @Test
    public void testMethodDelete() throws Exception {
        doNothing().when(userService).delete(userDto.getId());

        mvc.perform(delete("/users/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        doThrow(new NotFoundResource("Пользователь %d не найден".formatted(5)))
                .when(userService).delete(anyLong());

        mvc.perform(delete("/users/5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", equalTo("Пользователь 5 не найден")));

    }
}