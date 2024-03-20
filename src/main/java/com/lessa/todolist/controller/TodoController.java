package com.lessa.todolist.controller;

import com.lessa.todolist.domain.TodoItem;
import com.lessa.todolist.dto.ChangeDescriptionDto;
import com.lessa.todolist.dto.CreateItemDto;
import com.lessa.todolist.dto.TodoItemDto;
import com.lessa.todolist.service.TodoService;
import com.lessa.todolist.service.exception.ConflictException;
import com.lessa.todolist.service.exception.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService service;

    @PostMapping("/todos")
    public ResponseEntity<TodoItemDto> addItem(@RequestBody @Valid CreateItemDto createItemDto) throws ConflictException {
        var domain = service.add(createItemDto.toDomain());
        return ResponseEntity.ok(TodoItemDto.fromDomain(domain));
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<TodoItemDto> updateDescription(@PathVariable UUID id,
                                                         @RequestBody @Valid ChangeDescriptionDto changeDescriptionDto) throws ConflictException, NotFoundException {
        var domain = service.updateDescription(id, changeDescriptionDto.getDescription());
        return ResponseEntity.ok(TodoItemDto.fromDomain(domain));
    }

    @PutMapping("/todos/{id}/done")
    public ResponseEntity<TodoItemDto> markItemAsDone(@PathVariable UUID id) throws ConflictException, NotFoundException {
        var domain = service.markAsDone(id);
        return ResponseEntity.ok(TodoItemDto.fromDomain(domain));
    }

    @PutMapping("/todos/{id}/not-done")
    public ResponseEntity<TodoItemDto> markItemAsNotDone(@PathVariable UUID id) throws ConflictException, NotFoundException {
        var domain = service.markAsNotDone(id);
        return ResponseEntity.ok(TodoItemDto.fromDomain(domain));
    }
    @GetMapping("/todos")
    public ResponseEntity<Page<TodoItemDto>> getAll(@PageableDefault(size = 12) Pageable pageable) {
        var page = toPageDto(service.getAll(pageable));
        return ResponseEntity.ok(page);
    }

    @GetMapping("/todos/not-done")
    public ResponseEntity<Page<TodoItemDto>> getAllNotDone(@PageableDefault(size = 12) Pageable pageable) {
        var page = toPageDto(service.getNotDone(pageable));
        return ResponseEntity.ok(page);
    }

    private Page<TodoItemDto> toPageDto(Page<TodoItem> pageDomain) {
        return pageDomain.map(TodoItemDto::fromDomain);
    }


}
