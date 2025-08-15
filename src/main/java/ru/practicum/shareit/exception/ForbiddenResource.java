package ru.practicum.shareit.exception;

public class ForbiddenResource extends RuntimeException{
    public ForbiddenResource(String msg) {
        super(msg);
    }
}
