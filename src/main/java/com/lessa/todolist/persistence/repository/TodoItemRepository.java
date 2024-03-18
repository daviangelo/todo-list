package com.lessa.todolist.persistence.repository;

import com.lessa.todolist.domain.Status;
import com.lessa.todolist.persistence.entity.TodoItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TodoItemRepository extends JpaRepository<TodoItemEntity, UUID> {

    Page<TodoItemEntity> findAllByStatus(Status status, Pageable pageable);

    @Modifying
    //TODO @Query("")
    int updatePastDueItemsStatus(LocalDateTime actualDateTime);


}
