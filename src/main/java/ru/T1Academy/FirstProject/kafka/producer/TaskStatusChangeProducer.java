package ru.T1Academy.FirstProject.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.T1Academy.FirstProject.dto.event.TaskStatusChangeEvent;
import ru.T1Academy.FirstProject.exception.KafkaException;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskStatusChangeProducer {

    private final KafkaTemplate<String, TaskStatusChangeEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.task-status-update-topic}")
    private String taskStatusUpdateTopic;

    public void sendTaskStatusUpdate(TaskStatusChangeEvent taskStatusChangeEvent) {
        try {
            kafkaTemplate.send(taskStatusUpdateTopic, taskStatusChangeEvent.getId().toString(), taskStatusChangeEvent);
            kafkaTemplate.send(taskStatusUpdateTopic, taskStatusChangeEvent.getId().toString(), taskStatusChangeEvent);
            kafkaTemplate.send(taskStatusUpdateTopic, taskStatusChangeEvent.getId().toString(), taskStatusChangeEvent);
            kafkaTemplate.send(taskStatusUpdateTopic, taskStatusChangeEvent.getId().toString(), taskStatusChangeEvent);
            kafkaTemplate.send(taskStatusUpdateTopic, taskStatusChangeEvent.getId().toString(), taskStatusChangeEvent);
            kafkaTemplate.send(taskStatusUpdateTopic, taskStatusChangeEvent.getId().toString(), taskStatusChangeEvent);
            kafkaTemplate.send(taskStatusUpdateTopic, taskStatusChangeEvent.getId().toString(), taskStatusChangeEvent);
            kafkaTemplate.send(taskStatusUpdateTopic, taskStatusChangeEvent.getId().toString(), taskStatusChangeEvent);
        } catch (Exception exception) {
            log.error("Ошибка при отправке в топик {}: сообщения: {}", taskStatusUpdateTopic, taskStatusChangeEvent);
            throw new KafkaException("Kafka получил ошибку во время записи сообщения в топик: " + taskStatusUpdateTopic);
        }
        log.info("Продюсер успешно отправил в топик {} сообщение: {}", taskStatusUpdateTopic, taskStatusChangeEvent);
    }
}