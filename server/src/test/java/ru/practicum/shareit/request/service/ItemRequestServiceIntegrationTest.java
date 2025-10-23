package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.properties"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final EntityManager entityManager;

    @Test
    public void testIntegrationCreate() {
        UserCreate owner = UserCreate.builder()
                .name("Igor")
                .email("igor@yandex.ru")
                .build();
        UserDto userDto = userService.create(owner);

        ItemRequestCreate itemRequestCreate = ItemRequestCreate.builder()
                .description("Может кто то одолжить утюг?")
                .owner(userDto.getId())
                .build();

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(itemRequestCreate);
        TypedQuery<ItemRequest> query = entityManager
                .createQuery("Select ir from ItemRequest ir where ir.owner.id = :owner", ItemRequest.class);
        ItemRequest itemRequestCreated = query.setParameter("owner", userDto.getId())
                .getSingleResult();

        assertThat(itemRequestCreated.getId(), notNullValue());
        assertThat(itemRequestCreated.getDescription(), equalTo(itemRequestCreated.getDescription()));
    }
}
