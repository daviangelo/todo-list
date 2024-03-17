package com.lessa.todolist.service.impl;

import com.lessa.todolist.domain.TodoItem;
import com.lessa.todolist.persistence.repository.TodoItemRepository;
import com.lessa.todolist.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.util.UUID;

@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoItemRepository repository;
    private final Clock clock;

    @Override
    public TodoItem add(TodoItem item) {
        return null;
    }

    @Override
    public TodoItem updateDescription(UUID itemId, String description) {
        return null;
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
