package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.properties"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final ItemServiceImpl itemService;
    private final UserServiceImp userServiceImp;
    private final EntityManager entityManager;

    @Test
    public void testCreate() {
        UserCreate userCreate = UserCreate.builder()
                .name("Elena")
                .email("elena@email.ru")
                .build();
        UserDto userDto = userServiceImp.create(userCreate);

        ItemCreate itemCreate = ItemCreate.builder()
                .name("Пылесос")
                .description("Устройство для уборки комнаты")
                .available(true)
                .build();
        itemService.create(itemCreate, userDto.getId());

        TypedQuery<Item> query = entityManager
                .createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemCreated = query.setParameter("name", itemCreate.getName())
                .getSingleResult();

        assertThat(itemCreated.getId(), notNullValue());
        assertThat(itemCreated.getName(), equalTo(itemCreate.getName()));
        assertThat(itemCreated.getDescription(), equalTo(itemCreate.getDescription()));
        assertThat(itemCreated.isAvailable(), equalTo(itemCreate.getAvailable()));
    }
}
