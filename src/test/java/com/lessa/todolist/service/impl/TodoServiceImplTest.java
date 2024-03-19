package com.lessa.todolist.service.impl;

import com.lessa.todolist.domain.Status;
import com.lessa.todolist.domain.TodoItem;
import com.lessa.todolist.persistence.entity.TodoItemEntity;
import com.lessa.todolist.persistence.repository.TodoItemRepository;
import com.lessa.todolist.service.TimeService;
import com.lessa.todolist.service.exception.ConflictException;
import com.lessa.todolist.service.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    private final static LocalDateTime CURRENT_DATE = LocalDateTime.of(2024, 3, 17, 0, 0);
    private final static LocalDateTime AFTER_DATE = LocalDateTime.of(2024, 3, 18, 0, 0);

    @Mock
    TodoItemRepository repository;

    @Mock
    TimeService timeService;

    @InjectMocks
    TodoServiceImpl todoService;

    @Test
    void shouldAddNewItem() throws ConflictException {
        //given
        var itemToAdd = new TodoItem(null, "description", null, null,  AFTER_DATE, null);
        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.save(any())).then(returnsFirstArg());

        //when
        var result = todoService.add(itemToAdd);

        //then
        verify(timeService, times(1)).getLocalDateTime();
        verify(repository, times(1)).save(any());

        assertEquals("description", result.getDescription());
        assertEquals(Status.NOT_DONE, result.getStatus());
        assertEquals(CURRENT_DATE, result.getCreationDate());
        assertEquals(AFTER_DATE, result.getDueDate());
        assertNull(result.getDoneDate());
    }

    @Test
    void shouldThrowExceptionWhenAddNewItemWithPastDueDate() {
        //given
        var itemToAdd = new TodoItem(null, "description", null, null,  CURRENT_DATE, null);
        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);

        //when
        var exception = assertThrows(ConflictException.class, () ->
                todoService.add(itemToAdd));

        //then
        verify(timeService, times(1)).getLocalDateTime();
        verify(repository, times(0)).save(any());
        assertEquals("Cannot add item. Due date is earlier or equal the current date.", exception.getMessage());
    }

    @Test
    void shouldChangeDescription() throws NotFoundException, ConflictException{
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.NOT_DONE, CURRENT_DATE, AFTER_DATE, null);

        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));
        when(repository.save(any())).then(returnsFirstArg());

        //when
        var result = todoService.updateDescription(itemId,"new description");

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(1)).save(any());

        assertEquals("new description", result.getDescription());
        assertEquals(Status.NOT_DONE, result.getStatus());
        assertEquals(CURRENT_DATE, result.getCreationDate());
        assertEquals(AFTER_DATE, result.getDueDate());
        assertNull(result.getDoneDate());
    }

    @Test
    void shouldThrowExceptionWhenChangeDescriptionAndItemIdNotFound() {
        //given
        var itemId = UUID.randomUUID();
        when(repository.findById(itemId)).thenReturn(Optional.empty());

        //when
        var exception = assertThrows(NotFoundException.class, () ->
                todoService.updateDescription(itemId,"new description"));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(0)).save(any());
        assertEquals("Item not found with given id: " + itemId, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenChangeDescriptionOfPastDueItems() {
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.PAST_DUE, AFTER_DATE, CURRENT_DATE, null);
        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));

        //when
        var exception = assertThrows(ConflictException.class, () ->
                todoService.updateDescription(itemId,"new description"));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(0)).save(any());
        assertEquals("Cannot update the description. The item status is past due.", exception.getMessage());
    }

    @Test
    void shouldChangeItemStatusToPastDueAndThrowExceptionWhenChangeDescriptionOfPastDueItems() {
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.NOT_DONE, CURRENT_DATE, CURRENT_DATE, null);
        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));

        var todoItemEntityWithStatusUpdated = ArgumentCaptor.forClass(TodoItemEntity.class);

        //when
        var exception = assertThrows(ConflictException.class, () ->
                todoService.updateDescription(itemId,"new description"));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(1)).save(todoItemEntityWithStatusUpdated.capture());

        assertEquals(Status.PAST_DUE, todoItemEntityWithStatusUpdated.getValue().getStatus());
        assertEquals("Cannot update the description. The item status is past due.", exception.getMessage());
    }

    @Test
    void shouldMarkItemAsDone() throws NotFoundException, ConflictException {
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.NOT_DONE, CURRENT_DATE, AFTER_DATE, null);

        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));
        when(repository.save(any())).then(returnsFirstArg());

        //when
        var result = todoService.markAsDone(itemId);

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(1)).save(any());

        assertEquals("description", result.getDescription());
        assertEquals(Status.DONE, result.getStatus());
        assertEquals(CURRENT_DATE, result.getCreationDate());
        assertEquals(AFTER_DATE, result.getDueDate());
        assertEquals(CURRENT_DATE, result.getDoneDate());
    }

    @Test
    void shouldThrowExceptionWhenMarkItemAsDoneAndItemNotFoundWithId() {
        //given
        var itemId = UUID.randomUUID();
        when(repository.findById(itemId)).thenReturn(Optional.empty());

        //when
        var exception = assertThrows(NotFoundException.class, () ->
                todoService.markAsDone(itemId));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(0)).save(any());
        assertEquals("Item not found with given id: " + itemId, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMarkItemWithDoneStatusAsDone() {
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.DONE, CURRENT_DATE, AFTER_DATE, CURRENT_DATE);

        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));

        //when
        var exception = assertThrows(ConflictException.class, () ->
                todoService.markAsDone(itemId));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(0)).save(any());
        assertEquals("The item status is already done.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMarkItemWithPastDueStatusAsDone() {
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.PAST_DUE, AFTER_DATE, CURRENT_DATE, null);

        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));

        //when
        var exception = assertThrows(ConflictException.class, () ->
                todoService.markAsDone(itemId));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(0)).save(any());
        assertEquals("Cannot mark the item as done. The item status is past due.", exception.getMessage());
    }

    @Test
    void shouldChangeItemStatusToPastDueAndThrowExceptionWhenMarkItemAsDone() {
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.NOT_DONE, AFTER_DATE, CURRENT_DATE, null);

        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));

        var todoItemEntityWithStatusUpdated = ArgumentCaptor.forClass(TodoItemEntity.class);

        //when
        var exception = assertThrows(ConflictException.class, () ->
                todoService.markAsDone(itemId));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(1)).save(todoItemEntityWithStatusUpdated.capture());

        assertEquals(Status.PAST_DUE, todoItemEntityWithStatusUpdated.getValue().getStatus());
        assertEquals("Cannot mark the item as done. The item status is past due.", exception.getMessage());
    }

    @Test
    void shouldMarkItemAsNotDone() throws NotFoundException, ConflictException {
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.DONE, CURRENT_DATE, AFTER_DATE, CURRENT_DATE);

        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));
        when(repository.save(any())).then(returnsFirstArg());

        //when
        var result = todoService.markAsNotDone(itemId);

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(1)).save(any());

        assertEquals("description", result.getDescription());
        assertEquals(Status.NOT_DONE, result.getStatus());
        assertEquals(CURRENT_DATE, result.getCreationDate());
        assertEquals(AFTER_DATE, result.getDueDate());
        assertNull(result.getDoneDate());
    }

    @Test
    void shouldThrowExceptionWhenMarkItemAsNotDoneAndItemNotFoundWithId() {
        //given
        var itemId = UUID.randomUUID();
        when(repository.findById(itemId)).thenReturn(Optional.empty());

        //when
        var exception = assertThrows(NotFoundException.class, () ->
                todoService.markAsNotDone(itemId));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(0)).save(any());
        assertEquals("Item not found with given id: " + itemId, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMarkItemWithNotDoneStatusAsNotDone() {
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.NOT_DONE, CURRENT_DATE, AFTER_DATE, CURRENT_DATE);

        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));

        //when
        var exception = assertThrows(ConflictException.class, () ->
                todoService.markAsNotDone(itemId));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(0)).save(any());
        assertEquals("The item status is already not done.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMarkItemWithPastDueStatusAsNotDone() {
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.PAST_DUE, AFTER_DATE, CURRENT_DATE, null);

        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));

        //when
        var exception = assertThrows(ConflictException.class, () ->
                todoService.markAsNotDone(itemId));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(0)).save(any());
        assertEquals("Cannot mark the item as not done. The item status is past due.", exception.getMessage());
    }

    @Test
    void shouldChangeItemStatusToPastDueAndThrowExceptionWhenMarkItemAsNotDone() {
        //given
        var itemId = UUID.randomUUID();
        var itemToBeUpdated = new TodoItemEntity(itemId, "description", Status.DONE, AFTER_DATE, CURRENT_DATE, null);

        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findById(itemId)).thenReturn(Optional.of(itemToBeUpdated));

        var todoItemEntityWithStatusUpdated = ArgumentCaptor.forClass(TodoItemEntity.class);

        //when
        var exception = assertThrows(ConflictException.class, () ->
                todoService.markAsNotDone(itemId));

        //then
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(1)).save(todoItemEntityWithStatusUpdated.capture());

        assertEquals(Status.PAST_DUE, todoItemEntityWithStatusUpdated.getValue().getStatus());
        assertEquals("Cannot mark the item as not done. The item status is past due.", exception.getMessage());
    }

    @Test
    void shouldGetAllItems() {
        //given
        var itemId = UUID.randomUUID();
        var pageRequest = PageRequest.of(0, 1, Sort.by("creationDate").ascending());
        var retrievedPage = new PageImpl<>(List.of(new TodoItemEntity()));

        when(repository.findAll(pageRequest)).thenReturn(retrievedPage);

        //when
        todoService.getAll(pageRequest);

        //then
        verify(repository, times(1)).findAll(pageRequest);
    }

    @Test
    void shouldGetAllNotDoneItems() {
        //given
        var itemId = UUID.randomUUID();
        var pageRequest = PageRequest.of(0, 1, Sort.by("creationDate").ascending());
        var retrievedPage = new PageImpl<>(List.of(new TodoItemEntity(itemId, "description", Status.NOT_DONE, CURRENT_DATE, AFTER_DATE, null)));

        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findAllByStatus(Status.NOT_DONE, pageRequest)).thenReturn(retrievedPage);

        //when
        todoService.getNotDone(pageRequest);

        //then
        verify(repository, times(1)).findAllByStatus(Status.NOT_DONE, pageRequest);
    }

    @Test
    void shouldGetItem() {
        //given
        var itemId = UUID.randomUUID();
        var item = new TodoItemEntity(itemId, "description", Status.NOT_DONE, CURRENT_DATE, AFTER_DATE, null);

        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findById(itemId)).thenReturn(Optional.of(item));

        //when
        todoService.get(itemId);

        //then
        verify(repository, times(1)).findById(itemId);
    }

    @Test
    void shouldThrowExceptionWhenGetItemAndItemIdNotFound() {
        //given
        var itemId = UUID.randomUUID();

        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);
        when(repository.findById(itemId)).thenReturn(Optional.empty());

        //when
        var exception = assertThrows(NotFoundException.class, ()->
                todoService.get(itemId));


        //then
        verify(repository, times(1)).findById(itemId);
        assertEquals("Item not found with given id: " + itemId, exception.getMessage());
    }

    @Test
    void shouldUpdatePastDueItemsStatus() {
        //given
        when(timeService.getLocalDateTime()).thenReturn(CURRENT_DATE);

        //when
        todoService.updatePastDueItemsStatus();

        //then
        verify(repository, times(1)).updatePastDueItemsStatus(CURRENT_DATE);
    }
}