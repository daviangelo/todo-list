package com.lessa.todolist.service.impl;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class TimeServiceImplTest {

    @Test
    void shouldGetLocalDateTime() {
        //given
        var timeService = new TimeServiceImpl();

        //when
        var resultDateTime = timeService.getLocalDateTime();

        //then
        assertThat(LocalDateTime.now()).isCloseTo(resultDateTime, within(1, ChronoUnit.SECONDS));
    }

}