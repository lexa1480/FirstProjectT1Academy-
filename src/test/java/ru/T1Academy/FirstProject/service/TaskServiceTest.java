package ru.T1Academy.FirstProject.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.T1Academy.FirstProject.enums.TaskStatus;
import ru.T1Academy.FirstProject.exception.TaskNotFoundException;
import ru.T1Academy.FirstProject.kafka.producer.TaskStatusChangeProducer;
import ru.T1Academy.FirstProject.mapper.TaskMapper;
import ru.T1Academy.FirstProject.model.Task;
import ru.T1Academy.FirstProject.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Spy
    private TaskMapper taskMapper;

    @Mock
    private TaskStatusChangeProducer producer;

    @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("Получение всех Task")
    void getAllTasks() {
        List<Task> expectedTasks = List.of(new Task(), new Task());
        when(taskRepository.findAll()).
                thenReturn(expectedTasks);

        List<Task> actualTasks = taskService.getAllTasks();

        assertEquals(expectedTasks, actualTasks);
    }

    @Test
    @DisplayName("Получение Task по Id - Успешно")
    void getTaskById_ShouldReturnTask() {
        Task expectedTask = new Task();
        when(taskRepository.findById(any())).
                thenReturn(Optional.of(expectedTask));

        Task actualTask = taskService.getTaskById(1L);

        assertEquals(expectedTask, actualTask);
    }

    @Test
    @DisplayName("Получение Task по Id - Ошибка не найден")
    void getTaskById_ShouldThrowTaskNotFoundException() {
        when(taskRepository.findById(any())).
                thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.getTaskById(1L));
    }


    @Test
    @DisplayName("Создание Task")
    void createTask_ShouldSaveAndReturnTask() {
        Task taskToSave = new Task();
        when(taskRepository.save(any())).
                thenReturn(taskToSave);

        Task savedTask = taskService.createTask(new Task());

        assertEquals(taskToSave, savedTask);
    }

    @Test
    @DisplayName("Обновление Task - Успешно + Обновление статуса")
    void updateTask_ShouldUpdateTaskAndSendEvent() {
        Task oldTask = new Task(1L, TaskStatus.CREATED, "Old Title", "Old Description", 1L);
        Task newTask = new Task(1L, TaskStatus.COMPLETED, "New Title", "New Description", 2L);

        when(taskRepository.findById(any())).
                thenReturn(Optional.of(oldTask));
        when(taskRepository.save(any()))
                .thenReturn(newTask);

        Task result = taskService.updateTask(newTask);

        assertEquals(newTask.getTaskStatus(), result.getTaskStatus());
        assertEquals(newTask.getTitle(), result.getTitle());
        assertEquals(newTask.getDescription(), result.getDescription());
        assertEquals(newTask.getUserId(), result.getUserId());
        verify(producer).sendTaskStatusUpdate(any());
    }

    @Test
    @DisplayName("Обновление Task - Успешно + Без обновления статуса")
    void updateTask_ShouldUpdateTaskAndNotSendEvent() {
        Task oldTask = new Task(1L, TaskStatus.CREATED, "Old Title", "Old Description", 1L);
        Task newTask = new Task(1L, TaskStatus.CREATED, "New Title", "New Description", 2L);

        when(taskRepository.findById(any())).
                thenReturn(Optional.of(oldTask));
        when(taskRepository.save(any()))
                .thenReturn(newTask);

        Task result = taskService.updateTask(newTask);

        assertEquals(newTask.getTaskStatus(), result.getTaskStatus());
        assertEquals(newTask.getTitle(), result.getTitle());
        assertEquals(newTask.getDescription(), result.getDescription());
        assertEquals(newTask.getUserId(), result.getUserId());
        verify(producer, never()).sendTaskStatusUpdate(any());
    }

    @Test
    @DisplayName("Обновление Task - Ошибка не найден")
    void updateTask_ShouldThrowTaskNotFoundException() {
        when(taskRepository.findById(any())).
                thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTask(new Task()));
    }

    @Test
    @DisplayName("Удаление Task - Успешно")
    void deleteTaskById_ShouldDeleteTask() {
        when(taskRepository.existsById(any())).
                thenReturn(true);

        taskService.deleteTaskById(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Удаление Task - Ошибка не найден")
    void deleteTaskById_ShouldThrowTaskNotFoundException() {
        when(taskRepository.existsById(any())).
                thenReturn(false);

        assertThrows(TaskNotFoundException.class,
                () -> taskService.deleteTaskById(1L));
    }
}