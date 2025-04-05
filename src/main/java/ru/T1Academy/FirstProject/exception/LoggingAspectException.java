package ru.T1Academy.FirstProject.exception;

public class LoggingAspectException extends RuntimeException {
    public LoggingAspectException(String message) {
        super(message);
    }
}