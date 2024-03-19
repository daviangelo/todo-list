package com.lessa.todolist.persistence.repository;

import com.lessa.todolist.domain.Status;
import com.lessa.todolist.persistence.entity.TodoItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class TodoItemRepositoryTest {

    @Autowired
    private TodoItemRepository repository;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("findAllByStatusProvider")
    void findAllByStatus(Status statusFilter) {
        //given
        createTodoItemEntity(Status.NOT_DONE);
        createTodoItemEntity(Status.DONE);
        createTodoItemEntity(Status.PAST_DUE);

        //when
        var pageResult = repository.findAllByStatus(statusFilter, Pageable.unpaged());

        //then
        assertEquals(1, pageResult.getTotalElements());
        assertEquals(statusFilter, pageResult.getContent().get(0).getStatus());
    }

    public static Stream<Arguments> findAllByStatusProvider() {
        return Stream.of(
                Arguments.of(Status.NOT_DONE),
                Arguments.of(Status.DONE),
                Arguments.of(Status.PAST_DUE)
        );
    }

    @Test
    void updatePastDueItemsStatus() {
        //given
        var currentDate = LocalDateTime.of(2024, 3, 18, 0, 0);
        var dateToBeNotDue = LocalDateTime.of(2024, 3, 17, 12, 0);
        var dateToBeDue = LocalDateTime.of(2024, 3, 18, 0, 1);

        var itemIdNotDoneStatusUpdated = createTodoItemEntity(Status.NOT_DONE, dateToBeDue);
        var itemIdNotDoneStatusNotUpdated = createTodoItemEntity(Status.NOT_DONE, dateToBeNotDue);
        var itemIdDoneStatusNotUpdated = createTodoItemEntity(Status.DONE, dateToBeDue);

        //when
        var numberOfItemsUpdated = repository.updatePastDueItemsStatus(currentDate);

        //then
        assertEquals(1, numberOfItemsUpdated);

        var resultItemWithNotDoneStatusUpdated = repository.getReferenceById(itemIdNotDoneStatusUpdated);
        var resultItemWithNotDoneStatusNotUpdated = repository.getReferenceById(itemIdNotDoneStatusNotUpdated);
        var resultItemWithDoneStatusNotUpdated = repository.getReferenceById(itemIdDoneStatusNotUpdated);

        assertEquals(Status.PAST_DUE, resultItemWithNotDoneStatusUpdated.getStatus());
        assertEquals(Status.NOT_DONE, resultItemWithNotDoneStatusNotUpdated.getStatus());
        assertEquals(Status.DONE, resultItemWithDoneStatusNotUpdated.getStatus());
    }

    private void createTodoItemEntity(Status status) {
        var itemEntity = new TodoItemEntity();
        itemEntity.setStatus(status);
        repository.save(itemEntity);
    }

    private UUID createTodoItemEntity(Status status, LocalDateTime dueDate) {
        var itemEntity = new TodoItemEntity();
        itemEntity.setStatus(status);
        itemEntity.setDueDate(dueDate);
        return repository.save(itemEntity).getId();
    }

}