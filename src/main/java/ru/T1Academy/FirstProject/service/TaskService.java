package ru.T1Academy.FirstProject.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.T1Academy.FirstProject.aspect.annotation.LoggingAfterReturning;
import ru.T1Academy.FirstProject.aspect.annotation.LoggingAfterThrowing;
import ru.T1Academy.FirstProject.aspect.annotation.LoggingAround;
import ru.T1Academy.FirstProject.aspect.annotation.LoggingBefore;
import ru.T1Academy.FirstProject.dto.event.TaskStatusChangeEvent;
import ru.T1Academy.FirstProject.exception.TaskNotFoundException;
import ru.T1Academy.FirstProject.kafka.producer.TaskStatusChangeProducer;
import ru.T1Academy.FirstProject.mapper.TaskMapper;
import ru.T1Academy.FirstProject.model.Task;
import ru.T1Academy.FirstProject.repository.TaskRepository;

import java.util.List;

@LoggingBefore
@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final TaskStatusChangeProducer producer;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @LoggingAfterThrowing
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Задача с Id = " + id + " не найдена."));
    }

    @LoggingAfterReturning
    @Transactional
    public Task createTask(@Valid Task task) {
        return taskRepository.save(task);
    }

    @LoggingAround
    @Transactional
    public Task updateTask(@Valid Task task) {
        Task taskFound = taskRepository.findById(task.getId())
                        .orElseThrow(() -> new TaskNotFoundException("Задача с Id = " + task.getId() + " не найдена."));

        boolean bIsChangeState = !taskFound.getTaskStatus().equals(task.getTaskStatus());

        taskFound.setTaskStatus(task.getTaskStatus());
        taskFound.setTitle(task.getTitle());
        taskFound.setDescription(task.getDescription());
        taskFound.setUserId(task.getUserId());

        Task taskUpdated = taskRepository.save(taskFound);

        if(bIsChangeState)
        {
            TaskStatusChangeEvent taskStatusChangeEvent = taskMapper.toTaskStatusChangeEvent(taskUpdated);
            producer.sendTaskStatusUpdate(taskStatusChangeEvent);
        }

        return taskUpdated;
    }

    @LoggingAfterThrowing
    @LoggingAfterReturning
    @Transactional
    public void deleteTaskById(Long id) {
        if(!taskRepository.existsById(id))
        {
            throw new TaskNotFoundException("Задача с Id = " + id + " не найдена.");
        }

        taskRepository.deleteById(id);
    }
}