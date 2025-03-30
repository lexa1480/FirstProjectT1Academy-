package ru.T1Academy.FirstProject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.T1Academy.FirstProject.dto.request.TaskCreateRequest;
import ru.T1Academy.FirstProject.dto.request.TaskUpdateRequest;
import ru.T1Academy.FirstProject.dto.response.TaskResponse;
import ru.T1Academy.FirstProject.mapper.TaskMapper;
import ru.T1Academy.FirstProject.model.Task;
import ru.T1Academy.FirstProject.service.TaskService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class TaskController
{
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasks()
    {
        List<Task> taskList = taskService.getAllTasks();
        List<TaskResponse> taskResponseList = taskMapper.fromTaskList(taskList);

        return ResponseEntity
                .ok()
                .body(taskResponseList);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id)
    {
        Task task = taskService.getTaskById(id);
        TaskResponse taskResponse = taskMapper.fromTask(task);

        return ResponseEntity
                .ok()
                .body(taskResponse);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid TaskCreateRequest taskCreateRequest)
    {
        Task task = taskMapper.toTask(taskCreateRequest);
        Task taskCreated = taskService.createTask(task);
        TaskResponse taskResponse = taskMapper.fromTask(taskCreated);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskResponse);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody @Valid TaskUpdateRequest taskUpdateRequest)
    {
        Task task = taskMapper.toTask(taskUpdateRequest);
        task.setId(id);
        Task taskUpdated = taskService.updateTask(task);
        TaskResponse taskResponse = taskMapper.fromTask(taskUpdated);

        return ResponseEntity
                .ok()
                .body(taskResponse);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id)
    {
        taskService.deleteTaskById(id);

        return  ResponseEntity
                .noContent()
                .build();
    }
}






















