package ru.T1Academy.FirstProject.service;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.T1Academy.FirstProject.FirstProjectApplication;
import ru.T1Academy.FirstProject.enums.TaskStatus;
import ru.T1Academy.FirstProject.exception.TaskNotFoundException;
import ru.T1Academy.FirstProject.kafka.consumer.TaskStatusChangeConsumer;
import ru.T1Academy.FirstProject.kafka.producer.TaskStatusChangeProducer;
import ru.T1Academy.FirstProject.model.Task;
import ru.T1Academy.FirstProject.repository.TaskRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest(classes = FirstProjectApplication.class)
@Testcontainers
class IntegrationTest {

    @Container
    private static final KafkaContainer KAFKA =
            new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskStatusChangeProducer taskStatusChangeProducer;

    @MockitoSpyBean
    private TaskStatusChangeConsumer taskStatusChangeConsumer;

    @AfterEach
    void setup() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Получение всех Task")
    void getAllTasks() {
        Task oldTask = new Task();
        oldTask.setTaskStatus(TaskStatus.CREATED);
        oldTask.setTitle("Old title");
        oldTask.setDescription("Old description");
        oldTask.setUserId(6L);
        taskRepository.save(oldTask);

        List<Task> foundTasksList = taskService.getAllTasks();

        assertEquals(1, foundTasksList.size());
        Task foundTask = foundTasksList.get(0);
        assertNotNull(foundTask);
        assertEquals(oldTask.getId(), foundTask.getId());
        assertEquals(oldTask.getTaskStatus(), foundTask.getTaskStatus());
        assertEquals(oldTask.getTitle(), foundTask.getTitle());
        assertEquals(oldTask.getDescription(), foundTask.getDescription());
        assertEquals(oldTask.getUserId(), foundTask.getUserId());
    }

    @Test
    @DisplayName("Получение Task по Id - Успешно")
    void getTaskById_ShouldReturnTask() {
        Task oldTask = new Task();
        oldTask.setTaskStatus(TaskStatus.CREATED);
        oldTask.setTitle("Old title");
        oldTask.setDescription("Old description");
        oldTask.setUserId(6L);
        Task oldTaskSaved = taskRepository.save(oldTask);

        Task foundTask = taskService.getTaskById(oldTaskSaved.getId());
        assertNotNull(foundTask);
        assertEquals(oldTask.getId(), foundTask.getId());
        assertEquals(oldTask.getTaskStatus(), foundTask.getTaskStatus());
        assertEquals(oldTask.getTitle(), foundTask.getTitle());
        assertEquals(oldTask.getDescription(), foundTask.getDescription());
        assertEquals(oldTask.getUserId(), foundTask.getUserId());
    }

    @Test
    @DisplayName("Получение Task по Id - Ошибка не найден")
    void getTaskById_ShouldThrowTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class,
                () -> taskService.getTaskById(1L));
    }

    @Test
    @DisplayName("Создание Task")
    void createTask() {
        Task oldTask = new Task();
        oldTask.setTaskStatus(TaskStatus.CREATED);
        oldTask.setTitle("Old title");
        oldTask.setDescription("Old description");
        oldTask.setUserId(6L);

        Task createdTask = taskService.createTask(oldTask);

        assertNotNull(createdTask);
        assertEquals(oldTask.getId(), createdTask.getId());
        assertEquals(oldTask.getTaskStatus(), createdTask.getTaskStatus());
        assertEquals(oldTask.getTitle(), createdTask.getTitle());
        assertEquals(oldTask.getDescription(), createdTask.getDescription());
        assertEquals(oldTask.getUserId(), createdTask.getUserId());
    }

    @Test
    @DisplayName("Обновление Task - Успешно + Обновление статуса")
    void updateTask_ShouldUpdateTaskAndSendEvent() {
        Task oldTask = new Task();
        oldTask.setTaskStatus(TaskStatus.CREATED);
        oldTask.setTitle("Old title");
        oldTask.setDescription("Old description");
        oldTask.setUserId(6L);
        Task oldTaskSaved = taskRepository.save(oldTask);

        Task newTask = new Task();
        newTask.setId(oldTaskSaved.getId());
        newTask.setTaskStatus(TaskStatus.COMPLETED);
        newTask.setTitle("New title");
        newTask.setDescription("New description");
        newTask.setUserId(66L);

        Task updatedTask = taskService.updateTask(newTask);

        assertNotNull(updatedTask);
        assertEquals(newTask.getId(), updatedTask.getId());
        assertEquals(newTask.getTaskStatus(), updatedTask.getTaskStatus());
        assertEquals(newTask.getTitle(), updatedTask.getTitle());
        assertEquals(newTask.getDescription(), updatedTask.getDescription());
        assertEquals(newTask.getUserId(), updatedTask.getUserId());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        verify(taskStatusChangeConsumer).listen(any(), any(), any());
    }

    @Test
    @DisplayName("Обновление Task - Успешно + Без обновления статуса")
    void updateTask_ShouldUpdateTaskAndNotSendEvent() {
        Task oldTask = new Task();
        oldTask.setTaskStatus(TaskStatus.CREATED);
        oldTask.setTitle("Old title");
        oldTask.setDescription("Old description");
        oldTask.setUserId(6L);
        Task oldTaskSaved = taskRepository.save(oldTask);

        Task newTask = new Task();
        newTask.setId(oldTaskSaved.getId());
        newTask.setTaskStatus(TaskStatus.CREATED);
        newTask.setTitle("New title");
        newTask.setDescription("New description");
        newTask.setUserId(66L);

        Task updatedTask = taskService.updateTask(newTask);

        assertNotNull(updatedTask);
        assertEquals(newTask.getId(), updatedTask.getId());
        assertEquals(newTask.getTaskStatus(), updatedTask.getTaskStatus());
        assertEquals(newTask.getTitle(), updatedTask.getTitle());
        assertEquals(newTask.getDescription(), updatedTask.getDescription());
        assertEquals(newTask.getUserId(), updatedTask.getUserId());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        verify(taskStatusChangeConsumer, never()).listen(any(), any(), any());
    }

    @Test
    @DisplayName("Обновление Task - Ошибка не найден")
    void updateTask_ShouldThrowTaskNotFoundException() {
        Task newTask = new Task();
        newTask.setId(1L);
        newTask.setTaskStatus(TaskStatus.CREATED);
        newTask.setTitle("New title");
        newTask.setDescription("New description");
        newTask.setUserId(66L);

        assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTask(newTask));
    }

    @Test
    @DisplayName("Удаление Task - Успешно")
    void deleteTaskById_ShouldDeleteTask() {
        Task oldTask = new Task();
        oldTask.setTaskStatus(TaskStatus.CREATED);
        oldTask.setTitle("Old title");
        oldTask.setDescription("Old description");
        oldTask.setUserId(6L);
        Task oldTaskSaved = taskRepository.save(oldTask);

        taskService.deleteTaskById(oldTaskSaved.getId());

        assertFalse(taskRepository.findById(oldTaskSaved.getId()).isPresent());
    }

    @Test
    @DisplayName("Удаление Task - Ошибка не найден")
    void deleteTaskById_ShouldThrowTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class,
                () -> taskService.deleteTaskById(1L));
    }
}