package ru.practicum.shareit.user.service;

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
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {
    @MockBean
    private final UserRepository userRepository;
    private final UserServiceImp userServiceImp;
    private User user;

    @BeforeEach
    public void Before() {
        user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build();
    }

    @Test
    public void checkCorrectCreate() {
        UserCreate userCreate = UserCreate.builder()
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build();

        when(userRepository.save(isA(User.class)))
                .thenReturn(user);

        UserDto userDto = userServiceImp.create(userCreate);

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));

        Mockito.verify(userRepository, Mockito.times(1)).save(UserMapper.mapToUser(userCreate));
    }

    @Test
    public void checkCorrectGetUserMethod() {
        Optional<User> userOptional = Optional.of(user);

        when(userRepository.findById(1L)).thenReturn(userOptional);
        when(userRepository.findById(2L)).thenThrow(
                new NotFoundResource("Пользователь %d не найден".formatted(2L)));

        UserDto mapUser = UserMapper.mapToDto(user);

        UserDto userDto = userServiceImp.getUser(1);
        assertThat(userDto.getId(), equalTo(mapUser.getId()));
        assertThat(userDto.getName(), equalTo(mapUser.getName()));
        assertThat(userDto.getEmail(), equalTo(mapUser.getEmail()));

        NotFoundResource exception = Assertions.assertThrows(
                NotFoundResource.class,
                () -> userServiceImp.getUser(2));
        Assertions.assertEquals("Пользователь 2 не найден", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(2)).findById(anyLong());
    }

    @Test
    public void checkGetUsersMethod() {
        User user2 = User.builder()
                .id(2L)
                .name("Lena")
                .email("lena@yandex.ru")
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<UserDto> usersDto = userServiceImp.getUsers();
        assertThat(usersDto.size(), equalTo(2));
        assertThat(usersDto.get(0), equalTo(UserMapper.mapToDto(user)));
        assertThat(usersDto.get(1), equalTo(UserMapper.mapToDto(user2)));

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void checkUpdateMethod() {
        UserUpdate userUpdate = UserUpdate.builder()
                .id(5L)
                .name("Oleg")
                .email("oleg@yandex.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(5L)).thenThrow(
                new NotFoundResource("Пользователь %d не найден".formatted(5L)));

        NotFoundResource exception = Assertions.assertThrows(
                NotFoundResource.class,
                () -> userServiceImp.getUser(5));
        Assertions.assertEquals("Пользователь 5 не найден", exception.getMessage());

        userUpdate = userUpdate.toBuilder().id(1L).build();
        User newUser = User.builder()
                .id(1L)
                .name(userUpdate.getName())
                .email(userUpdate.getEmail())
                .build();
        when(userRepository.save(newUser)).thenReturn(newUser);

        UserDto userDto = userServiceImp.update(userUpdate);
        assertThat(userDto.getId(), equalTo(userUpdate.getId()));
        assertThat(userDto.getName(), equalTo(userUpdate.getName()));
        assertThat(userDto.getEmail(), equalTo(userUpdate.getEmail()));

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(newUser);
    }

    @Test
    public void testDeleteMethod() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userServiceImp.delete(user.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);

        when(userRepository.findById(5L)).thenThrow(
                new NotFoundResource("Пользователь %d не найден".formatted(5L)));

        NotFoundResource exception = Assertions.assertThrows(
                NotFoundResource.class,
                () -> userServiceImp.delete(5));
        Assertions.assertEquals("Пользователь 5 не найден", exception.getMessage());
    }


}