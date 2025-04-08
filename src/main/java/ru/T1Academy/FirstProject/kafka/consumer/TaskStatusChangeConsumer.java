package ru.T1Academy.FirstProject.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.T1Academy.FirstProject.dto.event.TaskStatusChangeEvent;
import ru.T1Academy.FirstProject.mapper.TaskMapper;
import ru.T1Academy.FirstProject.model.Task;
import ru.T1Academy.FirstProject.service.NotificationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskStatusChangeConsumer {
    private final TaskMapper taskMapper;
    private final NotificationService notificationService;


    @KafkaListener(id = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.task-status-update-topic}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload List<TaskStatusChangeEvent> taskStatusChangeEventList,
                       Acknowledgment ack,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        log.info("Получено {} сообщений из топика: {}", taskStatusChangeEventList.size(), topic);

        List<Task> taskList = taskMapper.toTaskList(taskStatusChangeEventList);
        notificationService.sendMessages(taskList);
        ack.acknowledge();

        log.info("Полученные сообщения обработаны.");
    }
}