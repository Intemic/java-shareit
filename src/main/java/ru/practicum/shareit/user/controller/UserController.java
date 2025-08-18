package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable @Positive Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid UserCreate user) {
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody @Valid UserUpdate user,
                          @PathVariable @Positive Long id) {
        user.setId(id);
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        userService.delete(id);
    }
}
