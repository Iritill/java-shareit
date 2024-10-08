package ru.practicum.shareit.requestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    private final String header = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    public ItemRequestDto getItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .description("TestItemRequestDescription")
                .requestor(null)
                .created(LocalDateTime.now())
                .items(List.of())
                .build();
    }

    @Test
    void create() throws Exception {
        ItemRequestDto itemRequestDto = getItemRequestDto();
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto(1L, "TestItemRequestRequestDescription");
        when(itemRequestService.create(any())).thenReturn(itemRequestDto);
        mockMvc.perform(post("/requests")
                        .header(header, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
        verify(itemRequestService, times(1)).create(any());
    }

    @Test
    void findAll() throws Exception {
        when(itemRequestService.findAllByUserId(anyLong())).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/requests")
                        .header(header, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).findAllByUserId(anyLong());
    }

    @Test
    void findItemRequestById() throws Exception {
        ItemRequestDto itemRequestDto = getItemRequestDto();
        when(itemRequestService.findItemRequestById(anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/1")
                        .header(header, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
        verify(itemRequestService, times(1)).findItemRequestById(anyLong());
    }

    @Test
    void findAllUsersItemRequest() throws Exception {
        when(itemRequestService.findAllUsersItemRequest(any())).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header(header, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemRequestService, times(1)).findAllUsersItemRequest(any());
    }
}