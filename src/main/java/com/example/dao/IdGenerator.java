package com.example.dao;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
@Service
class IdGenerator {

    private AtomicLong counter = new AtomicLong(0);

    long generateId() {
        return counter.incrementAndGet();
    }
}