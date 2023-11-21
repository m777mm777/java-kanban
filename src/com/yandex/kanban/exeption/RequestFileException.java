package com.yandex.kanban.exeption;

public class RequestFileException extends RuntimeException {
    public RequestFileException(final String message) {
        super(message);
    }
}
