package ru.practicum.shareit.exception;

public class NotFoundResource extends RuntimeException {
    public NotFoundResource(String msg) {
        super(msg);
    }
}
