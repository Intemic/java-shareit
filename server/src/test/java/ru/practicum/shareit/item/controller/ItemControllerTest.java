package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ForbiddenResource;
import ru.practicum.shareit.exception.NotApproved;
import ru.practicum.shareit.exception.NotFoundResource;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;
    private ItemDto itemDto;
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
        comment = Comment.builder()
                .id(1L)
                .item(1L)
                .author(userRental)
                .text("Все прошло отлично")
                .created(LocalDateTime.now())
                .build();
        List<CommentDto> listCommentDto = List.of(CommentMapper.mapToDto(comment));
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Перфоратор")
                .description("Можно сверлить стены")
                .available(true)
                .comments(listCommentDto)
                .owner(UserMapper.mapToDto(user))
                .build();

    }

    @Test
    public void testGetItem() throws Exception {
        when(itemService.getItem(itemDto.getId())).thenReturn(itemDto);

        mvc.perform(get("/items/" + itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.isAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.comments.length()", is(itemDto.getComments().size()))
                        // .andExpect(jsonPath("$.comments[0]", is(itemDto.getComments().get(0)))

                );

        when(itemService.getItem(5L))
                .thenThrow(new NotFoundResource("Не найдена вещь %d".formatted(5)));

        mvc.perform(get("/items/5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", equalTo("Не найдена вещь %d".formatted(5))));
    }

    @Test
    public void testGetItems() throws Exception {
        ItemDto itemDto2 = itemDto.toBuilder()
                .id(2L)
                .name("Кувшин")
                .description("Можно наливать воду")
                .comments(List.of())
                .build();

        when(itemService.getItems(user.getId())).thenReturn(List.of(itemDto, itemDto2));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", equalTo(itemDto.isAvailable())))
                .andExpect(jsonPath("$[0].owner.id", is(itemDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[0].comments.length()", is(itemDto.getComments().size())))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.isAvailable())))
                .andExpect(jsonPath("$[1].owner.id", is(itemDto2.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[1].comments.length()", is(0))
                );
    }

    @Test
    public void testMethodSearch() throws Exception {
        when(itemService.search("Перфоратор")).thenReturn(List.of(itemDto));
        when(itemService.search("Пылесос")).thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "Перфоратор"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.isAvailable())))
                .andExpect(jsonPath("$[0].owner.id", is(itemDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[0].comments.length()", is(itemDto.getComments().size()))
                );

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "Пылесос"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0)));
    }

    @Test
    public void testMethodCreate() throws Exception {
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
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.isAvailable())
                .requestId(itemRequest.getId())
                .build();

        when(itemService.create(itemCreate, user.getId())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.isAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.comments.length()", is(itemDto.getComments().size()))
                );

        itemCreate = ItemCreate.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.isAvailable())
                .build();
        when(itemService.create(itemCreate, user.getId())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.isAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.comments.length()", is(itemDto.getComments().size()))
                );
    }

    @Test
    public void testMethodCreateComment() throws Exception {
        CommentCreate commentCreate = CommentCreate.builder()
                .authorId(comment.getAuthor().getId())
                .itemId(itemDto.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();

        when(itemService.createComment(commentCreate)).thenReturn(commentDto);

        mvc.perform(post("/items/" + itemDto.getId() + "/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userRental.getId())
                        .content(mapper.writeValueAsString(commentCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                //  .andExpect(jsonPath("$.created", is(commentDto.getCreated())))
                .andExpect(jsonPath("$.text", is(commentDto.getText()))
                );

        when(itemService.createComment(commentCreate)).thenThrow(new NotApproved("Недопустимая операция"));

        mvc.perform(post("/items/" + itemDto.getId() + "/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userRental.getId())
                        .content(mapper.writeValueAsString(commentCreate)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Недопустимая операция"))
                );
    }

    @Test
    public void testMethodUpdate() throws Exception {
        ItemUpdate itemUpdate = ItemUpdate.builder()
                .id(itemDto.getId())
                .description(itemDto.getDescription() + " updated")
                .build();
        ItemDto updateItemDto = itemDto.toBuilder()
                .description(itemUpdate.getDescription())
                .build();

        when(itemService.update(itemUpdate, itemDto.getOwner().getId())).thenReturn(updateItemDto);

        mvc.perform(patch("/items/" + itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updateItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updateItemDto.isAvailable())));

        when(itemService.update(itemUpdate, itemDto.getOwner().getId()))
                .thenThrow(new ForbiddenResource("Отсутствуют полномочия на операцию"));

        mvc.perform(patch("/items/" + itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemUpdate)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error",is("Отсутствуют полномочия на операцию")));
    }
}