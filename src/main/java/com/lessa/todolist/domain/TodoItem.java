package com.lessa.todolist.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TodoItem {

    private UUID id;
    private String description;
    private Status status;
    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private LocalDateTime doneDate;
}
