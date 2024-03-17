package com.lessa.todolist.persistence.entity;

import com.lessa.todolist.domain.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "todo_item")
@Getter
@Setter
public class TodoItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private LocalDateTime doneDate;
}
