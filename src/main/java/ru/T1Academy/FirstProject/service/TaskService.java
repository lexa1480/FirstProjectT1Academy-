package ru.T1Academy.FirstProject.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.T1Academy.FirstProject.exception.TaskNotFoundException;
import ru.T1Academy.FirstProject.model.Task;
import ru.T1Academy.FirstProject.repository.TaskRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskService
{
    private final TaskRepository taskRepository;

    public List<Task> getAllTasks()
    {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id)
    {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Задача с Id = " + id + " не найдена."));
    }

    @Transactional
    public Task createTask(@Valid Task task)
    {
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(@Valid Task task)
    {
        Task taskFound = taskRepository.findById(task.getId())
                        .orElseThrow(() -> new TaskNotFoundException("Задача с Id = " + task.getId() + " не найдена."));

        taskFound.setTitle(task.getTitle());
        taskFound.setDescription(task.getDescription());
        taskFound.setUserId(task.getUserId());

        return taskRepository.save(taskFound);
    }

    @Transactional
    public void deleteTaskById(Long id)
    {
        if(!taskRepository.existsById(id))
        {
            throw new TaskNotFoundException("Задача с Id = " + id + " не найдена.");
        }

        taskRepository.deleteById(id);
    }
}















