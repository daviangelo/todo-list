package com.lessa.todolist.controller;

import com.lessa.todolist.domain.TodoItem;
import com.lessa.todolist.dto.ChangeDescriptionDto;
import com.lessa.todolist.dto.CreateItemDto;
import com.lessa.todolist.dto.TodoItemDto;
import com.lessa.todolist.service.TodoService;
import com.lessa.todolist.service.exception.ConflictException;
import com.lessa.todolist.service.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "TODO API", description = "API to manage TODO Items")
public class TodoController {

    private final TodoService service;

    @Operation(summary = "Create a new TODO Item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TODO Item created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TodoItemDto.class))}),
            @ApiResponse(responseCode = "409", description = "Cannot add item. Due date is earlier or equal the current date.", content = @Content)})
    @PostMapping("/todos")
    public ResponseEntity<TodoItemDto> addItem(@RequestBody @Valid CreateItemDto createItemDto) throws ConflictException {
        var domain = service.add(createItemDto.toDomain());
        return ResponseEntity.ok(TodoItemDto.fromDomain(domain));
    }

    @Operation(summary = "Update a TODO Item description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TODO Item description updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TodoItemDto.class))}),
            @ApiResponse(responseCode = "404", description = "Item not found with given id.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cannot change item description. Due date is earlier or equal the current date.", content = @Content)})
    @PutMapping("/todos/{id}")
    public ResponseEntity<TodoItemDto> updateDescription(@PathVariable UUID id,
                                                         @RequestBody @Valid ChangeDescriptionDto changeDescriptionDto) throws ConflictException, NotFoundException {
        var domain = service.updateDescription(id, changeDescriptionDto.getDescription());
        return ResponseEntity.ok(TodoItemDto.fromDomain(domain));
    }

    @Operation(summary = "Update a TODO Item status to done")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TODO Item status updated to done",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TodoItemDto.class))}),
            @ApiResponse(responseCode = "404", description = "Item not found with given id.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cannot change item status to done: Item status is already done. " +
                    "/ Due date is earlier or equal the current date.", content = @Content)})
    @PutMapping("/todos/{id}/done")
    public ResponseEntity<TodoItemDto> markItemAsDone(@PathVariable UUID id) throws ConflictException, NotFoundException {
        var domain = service.markAsDone(id);
        return ResponseEntity.ok(TodoItemDto.fromDomain(domain));
    }

    @Operation(summary = "Update a TODO Item status to not done")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TODO Item status updated to not done",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TodoItemDto.class))}),
            @ApiResponse(responseCode = "404", description = "Item not found with given id.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cannot change item status to not done: Item status is already not done. " +
                    "/ Due date is earlier or equal the current date.", content = @Content)})
    @PutMapping("/todos/{id}/not-done")
    public ResponseEntity<TodoItemDto> markItemAsNotDone(@PathVariable UUID id) throws ConflictException, NotFoundException {
        var domain = service.markAsNotDone(id);
        return ResponseEntity.ok(TodoItemDto.fromDomain(domain));
    }

    @Operation(summary = "Get TODO Item page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TODO Item page returned.", content = {@Content(mediaType = "application/json")})})
    @GetMapping("/todos")
    public ResponseEntity<Page<TodoItemDto>> getAll(@ParameterObject @PageableDefault(size = 12) Pageable pageable) {
        var page = toPageDto(service.getAll(pageable));
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get TODO Item page with not done status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TODO Item page with not done status returned.", content = {@Content(mediaType = "application/json")})})
    @GetMapping("/todos/not-done")
    public ResponseEntity<Page<TodoItemDto>> getAllNotDone(@ParameterObject @PageableDefault(size = 12) Pageable pageable) {
        var page = toPageDto(service.getNotDone(pageable));
        return ResponseEntity.ok(page);
    }


    @Operation(summary = "Get a TODO Item details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TODO Item returned",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TodoItemDto.class))}),
            @ApiResponse(responseCode = "404", description = "Item not found with given id.", content = @Content)})
    @GetMapping("/todos/{id}")
    public ResponseEntity<TodoItemDto> get(@PathVariable UUID id) throws NotFoundException {
        var domain = service.get(id);
        return ResponseEntity.ok(TodoItemDto.fromDomain(domain));
    }

    private Page<TodoItemDto> toPageDto(Page<TodoItem> pageDomain) {
        return pageDomain.map(TodoItemDto::fromDomain);
    }


}
