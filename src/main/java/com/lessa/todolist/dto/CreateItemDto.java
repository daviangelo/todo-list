package com.lessa.todolist.dto;

import com.lessa.todolist.domain.TodoItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateItemDto {

    @NotBlank(message = "the description must not be empty")
    private String description;

    @NotNull(message = "the due date must not be null")
    private LocalDateTime dueDate;

    public TodoItem toDomain() {
        return TodoItem.createNew(description, dueDate);
    }
}
