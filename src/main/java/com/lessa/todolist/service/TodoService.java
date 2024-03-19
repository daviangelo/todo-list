package com.lessa.todolist.service;

import com.lessa.todolist.domain.TodoItem;
import com.lessa.todolist.service.exception.ConflictException;
import com.lessa.todolist.service.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TodoService {

    TodoItem add(TodoItem item) throws ConflictException;

    TodoItem updateDescription(UUID itemId, String description) throws NotFoundException, ConflictException;

    TodoItem markAsDone(UUID itemId);

    TodoItem markAsNotDone(UUID itemId);

    Page<TodoItem> getAll(Pageable pageable);

    Page<TodoItem> getNotDone(Pageable pageable);

    TodoItem get(UUID itemId);

    void updatePastDueItemsStatus();

}
