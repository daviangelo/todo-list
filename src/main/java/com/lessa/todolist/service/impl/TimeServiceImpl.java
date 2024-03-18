package com.lessa.todolist.service.impl;

import com.lessa.todolist.service.TimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimeServiceImpl implements TimeService {

    @Override
    public LocalDateTime get() {
        return LocalDateTime.now();
    }
}
