package com.lessa.todolist.dto;

import com.lessa.todolist.domain.Status;
import com.lessa.todolist.domain.TodoItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TodoItemDto {

    private UUID id;
    private String description;
    private Status status;
    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private LocalDateTime doneDate;

    public static TodoItemDto fromDomain(TodoItem domain) {
        return new TodoItemDto(domain.getId(), domain.getDescription(), domain.getStatus(), domain.getCreationDate(),
                domain.getDueDate(), domain.getDoneDate());
    }
}
