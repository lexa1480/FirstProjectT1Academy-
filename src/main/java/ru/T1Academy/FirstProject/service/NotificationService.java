package ru.T1Academy.FirstProject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.T1Academy.FirstProject.aspect.annotation.LoggingBefore;
import ru.T1Academy.FirstProject.exception.TaskNotFoundException;
import ru.T1Academy.FirstProject.model.Task;
import ru.T1Academy.FirstProject.repository.TaskRepository;

import java.util.List;

@Slf4j
@LoggingBefore
@RequiredArgsConstructor
@Service
public class NotificationService {
    private final TaskRepository taskRepository;
    private final JavaMailSender mailSender;

    public void sendMessages(List<Task> taskList) {
        try {
            taskList.forEach(task -> {
                Task taskFound = taskRepository.findById(task.getId())
                        .orElseThrow(() -> new TaskNotFoundException("Задача с Id = " + task.getId() + " не найдена."));

                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("lexa.f1406@yandex.ru");
                message.setTo("lexa.f1406@yandex.ru");
                message.setSubject(taskFound.getTitle());
                message.setText(taskFound.getDescription());
                mailSender.send(message);
            });
        } catch (Exception e) {
            log.error("Kafka получил ошибку во время отправки уведомления об изменении статуса на почту.");
        }

    }
}
