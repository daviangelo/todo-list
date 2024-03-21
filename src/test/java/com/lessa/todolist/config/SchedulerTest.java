package com.lessa.todolist.config;

import com.lessa.todolist.service.impl.TodoServiceImpl;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = { "scheduler.enabled=true" })
class SchedulerTest {

    @SpyBean
    private TodoServiceImpl todoService;

    @Test
    void shouldWaitUntilTaskBeExecuted() {
        await()
                .atMost(Duration.ONE_SECOND)
                .untilAsserted(() -> verify(todoService, atLeast(1)).updatePastDueItemsStatus());
    }

}