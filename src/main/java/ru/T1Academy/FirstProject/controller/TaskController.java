package ru.T1Academy.FirstProject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.T1Academy.FirstProject.dto.request.TaskCreateRequest;
import ru.T1Academy.FirstProject.dto.request.TaskUpdateRequest;
import ru.T1Academy.FirstProject.dto.response.TaskResponse;
import ru.T1Academy.FirstProject.mapper.TaskMapper;
import ru.T1Academy.FirstProject.model.Task;
import ru.T1Academy.FirstProject.service.TaskService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @GetMapping("/tasks")
    public List<TaskResponse> getAllTasks() {
        List<Task> taskList = taskService.getAllTasks();

        return taskMapper.toTaskResponseList(taskList);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);

        return taskMapper.toTaskResponse(task);
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@RequestBody @Valid TaskCreateRequest taskCreateRequest) {
        Task task = taskMapper.toTask(taskCreateRequest);
        Task taskCreated = taskService.createTask(task);

        return taskMapper.toTaskResponse(taskCreated);
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @RequestBody @Valid TaskUpdateRequest taskUpdateRequest) {
        Task task = taskMapper.toTask(taskUpdateRequest);
        task.setId(id);
        Task taskUpdated = taskService.updateTask(task);

        return taskMapper.toTaskResponse(taskUpdated);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }
}