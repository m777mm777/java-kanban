package com.yandex.kanban.service;

public class TaskValidateException extends RuntimeException{

    public TaskValidateException() {
    }

    public TaskValidateException(String message) {
        super(message);
    }

    public TaskValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskValidateException(Throwable cause) {
        super(cause);
    }

    public TaskValidateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
