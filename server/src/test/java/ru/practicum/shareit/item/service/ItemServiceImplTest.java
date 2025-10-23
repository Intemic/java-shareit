package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenResource;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @MockBean
    private final ItemRepository itemRepository;
    @MockBean
    private final UserService userService;
    @MockBean
    private final BookingRepository bookingRepository;
    @MockBean
    private final CommentRepository commentRepository;
    @MockBean
    private final ItemRequestService itemRequestService;

    private final ItemServiceImpl itemService;

    private Item item;
    private User user;
    private User userRental;
    private Comment comment;

    @BeforeEach
    public void before() {
        user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@email")
                .build();
        userRental = User.builder()
                .id(99L)
                .name("Masha")
                .email("masha@email")
                .build();
        item = Item.builder()
                .id(1L)
                .owner(user)
                .name("Перфоратор")
                .description("Можно сверлить стены")
                .available(true)
                .build();
        comment = Comment.builder()
                .id(1L)
                .item(item.getId())
                .author(userRental)
                .text("Все прошло отлично")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    public void testMethodGetItem() {
        Optional<Item> optionalItem = Optional.of(item);
        when(itemRepository.findById(item.getId())).thenReturn(optionalItem);
        when(commentRepository.findAllByItem(item.getId())).thenReturn(List.of(comment));
        when(itemRepository.findById(2L)).thenThrow(
                new NotFoundResource("Не найдена вещь %d".formatted(2L)));

        ItemDto mapItemDto = ItemMapper.mapToDto(item);

        ItemDto itemDto = itemService.getItem(user.getId());
        assertThat(itemDto.getId(), equalTo(mapItemDto.getId()));
        assertThat(itemDto.getName(), equalTo(mapItemDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(mapItemDto.getDescription()));
        assertThat(itemDto.isAvailable(), equalTo(mapItemDto.isAvailable()));
        assertThat(itemDto.getOwner(), equalTo(mapItemDto.getOwner()));
        assertThat(itemDto.getComments().get(0).getId(), equalTo(comment.getId()));

        NotFoundResource exception = Assertions.assertThrows(
                NotFoundResource.class,
                () -> itemService.getItem(2L));
        Assertions.assertEquals("Не найдена вещь %d".formatted(2L), exception.getMessage());

        Mockito.verify(itemRepository, Mockito.times(2)).findById(anyLong());
    }

    @Test
    public void testMethodGetItems() {
        Item item2 = Item.builder()
                .id(2L)
                .owner(user)
                .name("Пылесос")
                .description("Сделает вашу комнату чище")
                .available(true)
                .build();
        List<ItemDto> itemListDto = List.of(ItemMapper.mapToDto(item), ItemMapper.mapToDto(item2));

        when(commentRepository.findAllByItemIn(Set.of(item.getId(), item2.getId())))
                .thenReturn(List.of(comment));
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(List.of(item, item2));
        when(itemRepository.findByOwnerId(55)).thenReturn(List.of());

        List<ItemDto> listItemDto = itemService.getItems(user.getId());
        assertThat(itemListDto.size(), equalTo(2));
        assertThat(listItemDto.get(0).getId(), equalTo(item.getId()));
        assertThat(listItemDto.get(0).getName(), equalTo(item.getName()));
        assertThat(listItemDto.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(listItemDto.get(0).isAvailable(), equalTo(item.isAvailable()));
        assertThat(listItemDto.get(0).getOwner().getId(), equalTo(item.getOwner().getId()));
        assertThat(listItemDto.get(0).getComments().size(), equalTo(1));
        assertThat(listItemDto.get(0).getComments().get(0).getId(), equalTo(comment.getId()));

        assertThat(listItemDto.get(1).getId(), equalTo(item2.getId()));
        assertThat(listItemDto.get(1).getName(), equalTo(item2.getName()));
        assertThat(listItemDto.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(listItemDto.get(1).isAvailable(), equalTo(item2.isAvailable()));
        assertThat(listItemDto.get(1).getOwner().getId(), equalTo(item2.getOwner().getId()));
        assertThat(listItemDto.get(1).getComments().size(), equalTo(0));

        listItemDto = itemService.getItems(55L);
        assertThat(listItemDto.size(), equalTo(0));

        Mockito.verify(commentRepository, times(1))
                .findAllByItemIn(Set.of(item.getId(), item2.getId()));
        Mockito.verify(itemRepository, times(1))
                .findByOwnerId(user.getId());
        Mockito.verify(itemRepository, times(1))
                .findByOwnerId(55);
    }

    @Test
    public void testMethodSearch() {
        when(itemRepository.search("Перфоратор")).thenReturn(List.of(item));
        when(itemRepository.search("Ручка")).thenReturn(List.of());
        List<ItemDto> listItemDto = itemService.search("Перфоратор");

        assertThat(listItemDto.size(), equalTo(1));
        ItemDto itemDto = listItemDto.get(0);
        assertThat(itemDto.getId(), equalTo(item.getId()));
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.isAvailable(), equalTo(item.isAvailable()));
        assertThat(itemDto.getOwner().getId(), equalTo(item.getOwner().getId()));

        assertThat(itemRepository.search("Ручка").size(), equalTo(0));
    }

    @Test
    public void testMethodCreate() {
        User userRequest = User.builder()
                .id(2L)
                .name("Elena")
                .email("elena@email")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .owner(userRequest)
                .created(LocalDateTime.now())
                .description("Что там нужно")
                .build();
        ItemCreate itemCreate = ItemCreate.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(itemRequest.getId())
                .build();

        when(userService.getOneUser(user.getId())).thenReturn(user);
        when(itemRequestService.getRequestOne(itemRequest.getId())).thenReturn(itemRequest);
        when(itemRepository.save(isA(Item.class))).thenReturn(item);

        ItemDto itemCreatedDto = itemService.create(itemCreate, user.getId());
        assertThat(itemCreatedDto.getId(), equalTo(item.getId()));
        assertThat(itemCreatedDto.getName(), equalTo(item.getName()));
        assertThat(itemCreatedDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemCreatedDto.getOwner().getId(), equalTo(user.getId()));
        assertThat(itemCreatedDto.getComments(), nullValue());
        assertThat(itemCreatedDto.getLastBooking(), nullValue());
        assertThat(itemCreatedDto.getNextBooking(), nullValue());

        Mockito.verify(userService, times(1)).getOneUser(user.getId());
        Mockito.verify(itemRequestService, times(1)).getRequestOne(itemRequest.getId());
        Mockito.verify(itemRepository, times(1)).save(isA(Item.class));
    }

    @Test
    public void testMethodUpdate() {
        ItemUpdate itemUpdate = ItemUpdate.builder()
                .id(item.getId())
                .description(item.getDescription() + "updated")
                .build();
        Optional<Item> optionalItem = Optional.of(item);
        Item updateItem = item.toBuilder().build();
        updateItem = ItemMapper.updateItem(itemUpdate, updateItem);

        when(itemRepository.findById(item.getId())).thenReturn(optionalItem);
        when(itemRepository.findById(5L)).thenThrow(
                new NotFoundResource("Не найдена вещь %d".formatted(5)));

        when(itemRepository.save(updateItem)).thenReturn(updateItem);

        ItemDto itemDto = itemService.update(itemUpdate, item.getOwner().getId());
        assertThat(itemDto.getId(), equalTo(updateItem.getId()));
        assertThat(itemDto.getName(), equalTo(updateItem.getName()));
        assertThat(itemDto.getDescription(), equalTo(updateItem.getDescription()));
        assertThat(itemDto.getOwner().getId(), equalTo(user.getId()));
        assertThat(itemDto.getComments(), nullValue());
        assertThat(itemDto.getLastBooking(), nullValue());
        assertThat(itemDto.getNextBooking(), nullValue());

        ForbiddenResource exceptionForbidden = Assertions.assertThrows(
                ForbiddenResource.class,
                () -> itemService.update(itemUpdate, 5L));
        Assertions.assertEquals("Отсутствуют полномочия на операцию", exceptionForbidden.getMessage());

        itemUpdate.setId(5L);
        NotFoundResource exception = Assertions.assertThrows(
                NotFoundResource.class,
                () -> itemService.update(itemUpdate, 5L));
        Assertions.assertEquals("Не найдена вещь %d".formatted(5), exception.getMessage());
    }

    @Test
    public void testMethodCreateComment() {
        CommentCreate commentCreate = CommentCreate.builder()
                .authorId(userRental.getId())
                .itemId(item.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
        Optional<Item> optionalItem = Optional.of(item);

        when(userService.getOneUser(commentCreate.getAuthorId())).thenReturn(userRental);
        when(userService.getOneUser(5L)).thenThrow(
                new NotFoundResource("Пользователь %d не найден".formatted(5)));
        when(itemRepository.findById(item.getId())).thenReturn(optionalItem);
        when(itemRepository.findById(5L)).thenThrow(
                new NotFoundResource("Не найдена вещь %d".formatted(5L)));

        when(bookingRepository.findAllByItemIdAndEndBeforeAndBookerIdAndStatus(anyLong(),
                isA(LocalDateTime.class), anyLong(), any()))
                .thenReturn(List.of(Booking.builder().build()));
        when(commentRepository.save(isA(Comment.class))).thenReturn(comment);

        CommentDto savedCommentDto = itemService.createComment(commentCreate);
        assertThat(savedCommentDto.getCreated(), equalTo(commentCreate.getCreated()));
        assertThat(savedCommentDto.getText(), equalTo(commentCreate.getText()));
        assertThat(savedCommentDto.getAuthorName(), equalTo(userRental.getName()));

        commentCreate.setItemId(5L);
        NotFoundResource exception = Assertions.assertThrows(
                NotFoundResource.class,
                () -> itemService.createComment(commentCreate));
        Assertions.assertEquals("Не найдена вещь %d".formatted(5L), exception.getMessage());

        commentCreate.setItemId(item.getId());
        commentCreate.setAuthorId(5L);
        exception = Assertions.assertThrows(
                NotFoundResource.class,
                () -> itemService.createComment(commentCreate));
        Assertions.assertEquals("Пользователь %d не найден".formatted(5), exception.getMessage());
    }
}