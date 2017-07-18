package com.example.dao;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
@Service
public class IdGenerator {

    private final AtomicLong counter = new AtomicLong(0);

    public long generateId() {
        return counter.incrementAndGet();
    }
}