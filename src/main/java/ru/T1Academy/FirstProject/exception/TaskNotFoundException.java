package ru.T1Academy.FirstProject.exception;

public class TaskNotFoundException extends RuntimeException
{
    public TaskNotFoundException(String message)
    {
        super(message);
    }
}
