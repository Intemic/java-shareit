package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest(properties = {"spring.config.location=classpath:application-test.properties"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final UserServiceImp userServiceImp;
    private final EntityManager entityManager;

    @Test
    public void testIntegrationCreate() {
        UserCreate userCreate = UserCreate.builder()
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build();

        userServiceImp.create(userCreate);
        TypedQuery<User> query = entityManager
                .createQuery("Select u from User u where u.email = :email", User.class);
        User userCreated = query.setParameter("email", userCreate.getEmail())
                .getSingleResult();

        assertThat(userCreated.getId(), notNullValue());
        assertThat(userCreated.getName(), equalTo(userCreate.getName()));
        assertThat(userCreated.getEmail(), equalTo(userCreate.getEmail()));
    }
}
