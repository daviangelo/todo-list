package com.lessa.todolist.service;

import com.lessa.todolist.domain.TodoItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TodoService {

    TodoItem add(TodoItem item);

    TodoItem updateDescription(UUID itemId, String description);

    TodoItem markAsDone(UUID itemId);

    TodoItem markAsNotDone(UUID itemId);

    Page<TodoItem> getAll(Pageable pageable);

    Page<TodoItem> getNotDone(Pageable pageable);

    TodoItem get(UUID itemId);

    void updatePastDueItemsStatus();

}
