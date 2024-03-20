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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public TodoItem updateDescription(UUID itemId, String description) throws NotFoundException, ConflictException {
        var item = findItemById(itemId);

        throwConflictExceptionWhenPastDueItem(item,
                "Cannot update the description. The item status is past due.");

        item.setDescription(description);
        var entity = repository.save(TodoItemEntity.toEntity(item));
        return entity.toDomain();
    }

    @Transactional
    @Override
    public TodoItem markAsDone(UUID itemId) throws NotFoundException, ConflictException {
        var item = findItemById(itemId);

        if (item.getStatus() == Status.DONE) {
            throw new ConflictException("The item status is already done.");
        }

        throwConflictExceptionWhenPastDueItem(item,
                "Cannot mark the item as done. The item due date has passed.");

        item.setStatus(Status.DONE);
        item.setDoneDate(timeService.getLocalDateTime());
        var entity = repository.save(TodoItemEntity.toEntity(item));
        return entity.toDomain();
    }

    @Transactional
    @Override
    public TodoItem markAsNotDone(UUID itemId) throws NotFoundException, ConflictException {
        var item = findItemById(itemId);

        if (item.getStatus() == Status.NOT_DONE) {
            throw new ConflictException("The item status is already not done.");
        }

        throwConflictExceptionWhenPastDueItem(item,
                "Cannot mark the item as not done. The item due date has passed.");

        item.setStatus(Status.NOT_DONE);
        item.setDoneDate(null);
        var entity = repository.save(TodoItemEntity.toEntity(item));
        return entity.toDomain();
    }

    @Override
    public Page<TodoItem> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(TodoItemEntity::toDomain);
    }

    @Override
    public Page<TodoItem> getNotDone(Pageable pageable) {
        return repository.findAllByStatus(Status.NOT_DONE, pageable).map(TodoItemEntity::toDomain);
    }

    @Override
    public TodoItem get(UUID itemId) throws NotFoundException {
        return findItemById(itemId);
    }

    @Override
    public void updatePastDueItemsStatus() {
        repository.updatePastDueItemsStatus(timeService.getLocalDateTime());
    }

    private TodoItem findItemById(UUID itemId) throws NotFoundException {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with given id: " + itemId))
                .toDomain();
    }

    private void throwConflictExceptionWhenPastDueItem(TodoItem item, String conflictMessage) throws ConflictException {
        var currentDate = timeService.getLocalDateTime();

        if (item.getStatus().equals(Status.PAST_DUE) || item.getDueDate().isBefore(currentDate) ||
                item.getDueDate().isEqual(currentDate)) {
            updateNotDoneToPastDue(item);
            throw new ConflictException(conflictMessage);
        }
    }

    private void updateNotDoneToPastDue(TodoItem item) {
        if (item.getStatus() == Status.NOT_DONE) {
            item.setStatus(Status.PAST_DUE);
            repository.save(TodoItemEntity.toEntity(item));
        }
    }

}
