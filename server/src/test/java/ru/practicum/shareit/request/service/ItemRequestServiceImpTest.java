package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImpTest {
    @MockBean
    private final ItemRequestRepository itemRequestRepository;
    @MockBean
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private Item item;
    private ItemRequest itemRequest;
    private User owner;
    private User ownerItemRequest;

    @BeforeEach
    public void before() {
        owner = User.builder()
                .id(2L)
                .name("Elena")
                .email("elena@yandex.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("Утюг")
                .description("Гладить вещи")
                .available(true)
                .build();
        ownerItemRequest = User.builder()
                .id(3L)
                .name("Vasya")
                .email("vasya@yandex.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(4L)
                .owner(ownerItemRequest)
                .created(LocalDateTime.now())
                .description("Может кто то одолжить утюг?")
                .build();
    }

    @Test
    public void testMethodGetRequest() {
        itemRequest.setItems(List.of(item));
        Optional<ItemRequest> optionalItemRequest = Optional.of(itemRequest);
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(optionalItemRequest);
        when(itemRequestRepository.findById(5L))
                .thenThrow(new NotFoundResource("Запрос %d не найден".formatted(5L)));

        ItemRequestDto itemRequestDto = itemRequestService.getRequest(itemRequest.getId());
        assertThat(itemRequestDto.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestDto.getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(itemRequestDto.getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestDto.getItems().getFirst().getId(), equalTo(item.getId()));

        NotFoundResource notFoundResource = Assertions.assertThrows(
                NotFoundResource.class,
                () -> itemRequestService.getRequest(5L));
        Assertions.assertEquals("Запрос %d не найден".formatted(5L), notFoundResource.getMessage());
    }

    @Test
    public void testMethodGetYourRequests() {
        itemRequest.setItems(List.of(item));
        when(itemRequestRepository.findAllByOwnerIdOrderByCreated(ownerItemRequest.getId()))
                .thenReturn(List.of(itemRequest));
        when(itemRequestRepository.findAllByOwnerIdOrderByCreated(5L))
                .thenReturn(List.of());

        List<ItemRequestDto> listItemRequestDto = itemRequestService.getYourRequests(ownerItemRequest.getId());
        assertThat(listItemRequestDto.size(), equalTo(1));
        assertThat(listItemRequestDto.getFirst().getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(listItemRequestDto.getFirst().getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(listItemRequestDto.getFirst().getItems().size(), equalTo(itemRequest.getItems().size()));
        assertThat(listItemRequestDto.getFirst().getItems().getFirst().getId(), equalTo(item.getId()));

        listItemRequestDto = itemRequestService.getYourRequests(5L);
        assertThat(listItemRequestDto.size(), equalTo(0));
    }

    @Test
    public void testMethodGetRequestsExcludingYour() {
        User ownerItemRequest2 = User.builder()
                .id(5L)
                .name("Katya")
                .email("katya@yandex.ru")
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(6L)
                .owner(ownerItemRequest2)
                .created(LocalDateTime.now())
                .description("У кого есть свободный перфоратор?")
                .items(List.of(item))
                .build();

        when(itemRequestRepository.findAllByOwnerIdNotOrderByCreated(ownerItemRequest.getId()))
                .thenReturn(List.of(itemRequest2));
        when(itemRequestRepository.findAllByOwnerIdNotOrderByCreated(ownerItemRequest2.getId()))
                .thenReturn(List.of());

        List<ItemRequestDto> listItemRequestDto = itemRequestService.getRequestsExcludingYour(ownerItemRequest.getId());
        assertThat(listItemRequestDto.size(), equalTo(1));
        assertThat(listItemRequestDto.getFirst().getId(), equalTo(itemRequest2.getId()));

        listItemRequestDto = itemRequestService.getRequestsExcludingYour(ownerItemRequest2.getId());
        assertThat(listItemRequestDto.size(), equalTo(0));
    }

    @Test
    public void testMethodCreateRequest() {
        ItemRequestCreate itemRequestCreate = ItemRequestCreate.builder()
                .description(itemRequest.getDescription())
                .owner(ownerItemRequest.getId())
                .build();

        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        when(userService.getOneUser(ownerItemRequest.getId())).thenReturn(ownerItemRequest);

        when(userService.getOneUser(55L))
                .thenThrow(new NotFoundResource("Пользователь %d не найден".formatted(55L)));

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(itemRequestCreate);
        assertThat(itemRequestDto.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestDto.getCreated(), equalTo(itemRequest.getCreated()));

        itemRequestCreate.setOwner(55L);
        NotFoundResource notFoundResource = Assertions.assertThrows(
                NotFoundResource.class,
                () -> itemRequestService.createRequest(itemRequestCreate));
        Assertions.assertEquals("Пользователь %d не найден".formatted(55L), notFoundResource.getMessage());
    }
}