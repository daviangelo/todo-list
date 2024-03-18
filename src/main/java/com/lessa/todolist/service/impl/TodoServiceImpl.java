package com.lessa.todolist.service.impl;

import com.lessa.todolist.domain.TodoItem;
import com.lessa.todolist.persistence.repository.TodoItemRepository;
import com.lessa.todolist.service.TimeService;
import com.lessa.todolist.service.TodoService;
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
