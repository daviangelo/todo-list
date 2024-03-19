package com.lessa.todolist.persistence.repository;

import com.lessa.todolist.domain.Status;
import com.lessa.todolist.persistence.entity.TodoItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TodoItemRepository extends JpaRepository<TodoItemEntity, UUID> {

    Page<TodoItemEntity> findAllByStatus(Status status, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update TodoItemEntity item set item.status = 'PAST_DUE' where item.status = 'NOT_DONE' and item.dueDate >= :currentDateTime")
    int updatePastDueItemsStatus(@Param("currentDateTime") LocalDateTime currentDateTime);

}
