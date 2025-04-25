package ru.T1Academy.FirstProject.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.T1Academy.FirstProject.model.Task;
import ru.T1Academy.FirstProject.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    private ListAppender<ILoggingEvent> logWatcher;

    @BeforeEach
    void setup() {
        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(NotificationService.class)).addAppender(logWatcher);
    }

    @AfterEach
    void teardown() {
        ((Logger) LoggerFactory.getLogger(NotificationService.class)).detachAndStopAllAppenders();
    }

    @Test
    @DisplayName("Отправка сообщений - Успешно + отправка письма")
    void sendMessages_ShouldSendEmailForEachTask() {
        when(taskRepository.findById(any())).
                thenReturn(Optional.of(new Task()));

        notificationService.sendMessages(List.of(new Task()));

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Отправка сообщений - Ошибка не найден")
    void sendMessages_ShouldThrowTaskNotFoundException() {
        when(taskRepository.findById(any())).
                thenReturn(Optional.empty());

        notificationService.sendMessages(List.of(new Task()));

        assertEquals(1, logWatcher.list.size());
        assertEquals(Level.ERROR, logWatcher.list.get(logWatcher.list.size() - 1).getLevel());
        assertEquals("Kafka получил ошибку во время отправки уведомления об изменении статуса на почту.",
                logWatcher.list.get(logWatcher.list.size() - 1).getFormattedMessage());
    }

    @Test
    @DisplayName("Отправка сообщений - Ошибка отправки письма")
    void sendMessages_ShouldLogErrorOnMailException() {
        when(taskRepository.findById(any())).
                thenReturn(Optional.of(new Task()));
        doThrow(new MailSendException("Ошибка SMTP")).
                when(mailSender).
                send(any(SimpleMailMessage.class));

        notificationService.sendMessages(List.of(new Task()));

        assertEquals(1, logWatcher.list.size());
        assertEquals(Level.ERROR, logWatcher.list.get(0).getLevel());
    }
}