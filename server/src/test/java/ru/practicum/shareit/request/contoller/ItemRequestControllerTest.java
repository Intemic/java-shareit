package ru.practicum.shareit.request.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    public void before() {
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Может кто то одолжить утюг?")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    public void testMethodGetRequest() throws Exception {
        when(itemRequestService.getRequest(itemRequestDto.getId())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/" + itemRequestDto.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", equalTo(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        equalTo(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));

        MvcResult result = mvc.perform(get("/requests/5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        Assertions.assertEquals( "", result.getResponse().getContentAsString());
    }

    @Test
    public void testMethodGetYourRequests() throws Exception {
       when(itemRequestService.getYourRequests(2L)).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", equalTo(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created",
                        equalTo(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @Test
    public void testMethodGetRequestsExcludingYour() throws Exception {
        when(itemRequestService.getRequestsExcludingYour(2L)).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", equalTo(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created",
                        equalTo(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @Test
    public void testMethodCreateRequest() throws Exception {
        ItemRequestCreate itemRequestCreate = ItemRequestCreate.builder()
                .description(itemRequestDto.getDescription())
                .owner(2L)
                .build();
        when(itemRequestService.createRequest(itemRequestCreate)).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestCreate))
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", equalTo(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        equalTo(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }
}