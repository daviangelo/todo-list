package com.lessa.todolist.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoItem {

    private UUID id;
    private String description;
    private Status status;
    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private LocalDateTime doneDate;

    public static TodoItem createNew(String description, LocalDateTime dueDate) {
        var todoItem = new TodoItem();
        todoItem.setDescription(description);
        todoItem.setDueDate(dueDate);
        return todoItem;
    }
}
