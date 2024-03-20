package com.lessa.todolist.controller;

import com.lessa.todolist.domain.Status;
import com.lessa.todolist.dto.ChangeDescriptionDto;
import com.lessa.todolist.dto.CreateItemDto;
import com.lessa.todolist.dto.TodoItemDto;
import com.lessa.todolist.persistence.entity.TodoItemEntity;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

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
        var dto = new CreateItemDto();
        dto.setDescription("description");
        dto.setDueDate(AFTER_DATE);

        Mockito.when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);

        //when
        var responseEntity = restTemplate.postForEntity(createUrlWithPort("/todos"), dto, TodoItemDto.class);

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

        assertTrue(repository.findById(responseBody.getId()).isPresent());
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
        assertTrue(repository.findAll().isEmpty());
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

    @Test
    void shouldChangeDescription() {
        //given
        var itemId = saveItem("description", Status.NOT_DONE, CURRENT_DATE, AFTER_DATE, null);
        var dto = new ChangeDescriptionDto();
        dto.setDescription("new description");

        Mockito.when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);

        //when
        var responseEntity = restTemplate.exchange(createUrlWithPort("/todos/" + itemId), HttpMethod.PUT,
                new HttpEntity<>(dto), TodoItemDto.class);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        var responseBody = responseEntity.getBody();

        assertNotNull(responseBody);
        assertNotNull(responseBody.getId());
        assertEquals("new description", responseBody.getDescription());
        assertEquals(Status.NOT_DONE, responseBody.getStatus());
        assertEquals(CURRENT_DATE, responseBody.getCreationDate());
        assertEquals(AFTER_DATE, responseBody.getDueDate());
        assertNull(responseBody.getDoneDate());

        var itemInDatabase =  repository.findById(itemId);
        assertTrue(itemInDatabase.isPresent());
        assertEquals("new description", itemInDatabase.get().getDescription());
    }

    @Test
    void shouldFailChangeDescriptionWhenDescriptionIsBlank() {
        //given
        var expectedResponseBody = Map.of("description", "the description must not be empty");

        //when
        var responseEntity = restTemplate.exchange(createUrlWithPort("/todos/" + UUID.randomUUID()), HttpMethod.PUT,
                new HttpEntity<>(new ChangeDescriptionDto()), Map.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(expectedResponseBody, responseEntity.getBody());
    }

    @Test
    void shouldThrowExceptionWhenChangeDescriptionAndItemIdNotFound() {
        //given
        var dto = new ChangeDescriptionDto();
        dto.setDescription("new description");

        var id = UUID.randomUUID();
        var expectedResponseBody = "Item not found with given id: " + id;

        //when
        var responseEntity = restTemplate.exchange(createUrlWithPort("/todos/" + id), HttpMethod.PUT,
                new HttpEntity<>(dto), String.class);

        //then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(expectedResponseBody, responseEntity.getBody());
    }

    @Test
    void shouldFailWhenChangeDescriptionOfPastDueItems() {
        var itemId = saveItem("description", Status.PAST_DUE, AFTER_DATE, CURRENT_DATE, null);
        var dto = new ChangeDescriptionDto();
        dto.setDescription("new description");
        var expectedResponseBody = "Cannot update the description. The item status is past due.";

        Mockito.when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);

        //when
        var responseEntity = restTemplate.exchange(createUrlWithPort("/todos/" + itemId), HttpMethod.PUT,
                new HttpEntity<>(dto), String.class);

        //then
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals(expectedResponseBody, responseEntity.getBody());

        var itemInDatabase =  repository.findById(itemId);
        assertTrue(itemInDatabase.isPresent());
        assertEquals("description", itemInDatabase.get().getDescription());
    }

    @Test
    void shouldChangeItemStatusToPastDueAndFailWhenChangeDescriptionOfPastDueItems() {
        var itemId = saveItem("description", Status.NOT_DONE, AFTER_DATE, CURRENT_DATE, null);
        var dto = new ChangeDescriptionDto();
        dto.setDescription("new description");
        var expectedResponseBody = "Cannot update the description. The item status is past due.";

        Mockito.when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);

        //when
        var responseEntity = restTemplate.exchange(createUrlWithPort("/todos/" + itemId), HttpMethod.PUT,
                new HttpEntity<>(dto), String.class);

        //then
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals(expectedResponseBody, responseEntity.getBody());

        var itemInDatabase =  repository.findById(itemId);
        assertTrue(itemInDatabase.isPresent());
        assertEquals("description", itemInDatabase.get().getDescription());
        assertEquals(Status.PAST_DUE, itemInDatabase.get().getStatus());
    }



    private UUID saveItem(String description, Status status, LocalDateTime creationDate, LocalDateTime dueDate,
                          LocalDateTime doneDate) {
        var entity = new TodoItemEntity(null, description, status, creationDate, dueDate, doneDate);

        return repository.save(entity).getId();
    }

    private String createUrlWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
