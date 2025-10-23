package ru.practicum.shareit.exception;

public class NotApproved extends RuntimeException {
    public NotApproved(String msg) {
        super(msg);
    }
}
