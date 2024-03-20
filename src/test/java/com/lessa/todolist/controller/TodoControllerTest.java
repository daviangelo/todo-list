package com.lessa.todolist.controller;

import com.lessa.todolist.domain.Status;
import com.lessa.todolist.dto.CreateItemDto;
import com.lessa.todolist.dto.TodoItemDto;
import com.lessa.todolist.persistence.repository.TodoItemRepository;
import com.lessa.todolist.service.TimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerTest {

    private final static LocalDateTime CURRENT_DATE = LocalDateTime.of(2024, 3, 17, 0, 0);
    private final static LocalDateTime AFTER_DATE = LocalDateTime.of(2024, 3, 18, 0, 0);

    @LocalServerPort
    private int port;

    @MockBean
    private TimeService timeService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TodoItemRepository repository;
    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldAddNewItem() {
        //given
        var createItemDto = new CreateItemDto();
        createItemDto.setDescription("description");
        createItemDto.setDueDate(AFTER_DATE);

        Mockito.when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);

        //when
        var responseEntity = restTemplate.postForEntity(createUrlWithPort("/todos"), createItemDto, TodoItemDto.class);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        var responseBody = responseEntity.getBody();

        assertNotNull(responseBody);
        assertNotNull(responseBody.getId());
        assertEquals("description", responseBody.getDescription());
        assertEquals(Status.NOT_DONE, responseBody.getStatus());
        assertEquals(CURRENT_DATE, responseBody.getCreationDate());
        assertEquals(AFTER_DATE, responseBody.getDueDate());
        assertNull(responseBody.getDoneDate());
    }

    @Test
    void shouldAddNewItemFailWhenCreationItemDtoDoNotHaveDueDateAndDescription() {
        //given
        var expectedResponseBody = Map.of("dueDate", "the due date must not be null",
                "description", "the description must not be empty");

        //when
        var responseEntity = restTemplate.postForEntity(createUrlWithPort("/todos"), new CreateItemDto(), Map.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(expectedResponseBody, responseEntity.getBody());
    }

    @Test
    void shouldFailWhenAddNewItemWithPastDueDate() {
        //given
        var createItemDto = new CreateItemDto();
        createItemDto.setDescription("description");
        createItemDto.setDueDate(CURRENT_DATE);

        Mockito.when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        var expectedResponseBody = "Cannot add item. Due date is earlier or equal the current date.";

        //when
        var responseEntity = restTemplate.postForEntity(createUrlWithPort("/todos"), createItemDto, String.class);

        //then
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals(expectedResponseBody, responseEntity.getBody());
    }

    private String createUrlWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
