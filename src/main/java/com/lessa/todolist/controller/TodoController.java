package com.lessa.todolist.controller;

import com.lessa.todolist.dto.CreateItemDto;
import com.lessa.todolist.dto.TodoItemDto;
import com.lessa.todolist.service.TodoService;
import com.lessa.todolist.service.exception.ConflictException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService service;

    @PostMapping("/todos")
    public ResponseEntity<TodoItemDto> addItem(@RequestBody @Valid CreateItemDto createItemDto) throws ConflictException {
        var domain = service.add(createItemDto.toDomain());
        return ResponseEntity.ok(TodoItemDto.fromDomain(domain));
    }

}
