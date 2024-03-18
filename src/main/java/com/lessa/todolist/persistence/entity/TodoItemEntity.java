package com.lessa.todolist.persistence.entity;

import com.lessa.todolist.domain.Status;
import com.lessa.todolist.domain.TodoItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "todo_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private LocalDateTime doneDate;

    public static TodoItemEntity toEntity(TodoItem domain) {
        return new TodoItemEntity(domain.getId(), domain.getDescription(), domain.getStatus(), domain.getCreationDate(),
                domain.getDueDate(), domain.getDoneDate());
    }

    public TodoItem toDomain() {
        return new TodoItem(this.getId(), this.getDescription(), this.getStatus(), this.getCreationDate(),
                this.getDueDate(), this.getDoneDate());
    }
}
