package com.lessa.todolist.service.impl;

import com.lessa.todolist.domain.Status;
import com.lessa.todolist.domain.TodoItem;
import com.lessa.todolist.persistence.entity.TodoItemEntity;
import com.lessa.todolist.persistence.repository.TodoItemRepository;
import com.lessa.todolist.service.TimeService;
import com.lessa.todolist.service.TodoService;
import com.lessa.todolist.service.exception.ConflictException;
import com.lessa.todolist.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoItemRepository repository;
    private final TimeService timeService;

    @Override
    public TodoItem add(TodoItem item) throws ConflictException {
        var currentDate = timeService.getLocalDateTime();
        if (item.getDueDate().isBefore(currentDate) || item.getDueDate().isEqual(currentDate)) {
            throw new ConflictException("Cannot add item. Due date is earlier or equal the current date.");
        }

        item.setStatus(Status.NOT_DONE);
        item.setCreationDate(currentDate);

        var entity = repository.save(TodoItemEntity.toEntity(item));
        return entity.toDomain();
    }

    @Override
    public TodoItem updateDescription(UUID itemId, String description) throws NotFoundException, ConflictException {
        var item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with given id: " + itemId))
                .toDomain();

        var currentDate = timeService.getLocalDateTime();

        if (item.getStatus().equals(Status.PAST_DUE)) {
            throw new ConflictException("Cannot update the description. The item status is past due.");
        } else if (item.getDueDate().isBefore(currentDate) || item.getDueDate().isEqual(currentDate)) {
            item.setStatus(Status.PAST_DUE);
            repository.save(TodoItemEntity.toEntity(item));
            throw new ConflictException("Cannot update the description. The item status is past due.");
        }

        item.setDescription(description);
        var entity = repository.save(TodoItemEntity.toEntity(item));
        return entity.toDomain();
    }

    @Override
    public TodoItem markAsDone(UUID itemId) {
        return null;
    }

    @Override
    public TodoItem markAsNotDone(UUID itemId) {
        return null;
    }

    @Override
    public Page<TodoItem> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<TodoItem> getNotDone(Pageable pageable) {
        return null;
    }

    @Override
    public TodoItem get(UUID itemId) {
        return null;
    }

    @Override
    public void updatePastDueItemsStatus() {

    }
}
