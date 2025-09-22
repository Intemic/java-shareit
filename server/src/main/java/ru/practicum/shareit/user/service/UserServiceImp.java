package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictResource;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    public User getOneUser(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            throw new NotFoundResource("Пользователь %d не найден".formatted(id));

        return userOptional.get();
    }

    public void checkData(User user) {
        Long userId = null;
        Optional<User> optionalUser = userRepository.findByNameContainingIgnoreCase(user.getName());
        if (optionalUser.isPresent())
            if (user.getId() == null || !user.equals(optionalUser.get()))
                throw new ConflictResource("Пользователь с name - %s уже присутствует".formatted(user.getName()));


        optionalUser = userRepository.findByEmailContainingIgnoreCase(user.getEmail());
        if (optionalUser.isPresent())
            if (user.getId() == null || !user.equals(optionalUser.get()))
                throw new ConflictResource("Пользователь с email - %s уже присутствует".formatted(user.getEmail()));
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.mapToDto(getOneUser(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto create(UserCreate userCreate) {
        User user = UserMapper.mapToUser(userCreate);
        checkData(user);
        return UserMapper.mapToDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto update(UserUpdate userUpdate) {
        User oldUser = getOneUser(userUpdate.getId());
        User updateUser = UserMapper.updateUser(userUpdate, oldUser);
        checkData(updateUser);
        return UserMapper.mapToDto(userRepository.save(updateUser));
    }

    @Transactional
    @Override
    public void delete(long id) {
        getOneUser(id);
        userRepository.deleteById(id);
    }
}
