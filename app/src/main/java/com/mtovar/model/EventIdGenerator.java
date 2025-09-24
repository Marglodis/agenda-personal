package com.mtovar.model;

import java.util.UUID;

public class EventIdGenerator {
    public static String generateEventId() {
        return UUID.randomUUID().toString();
    }
}
