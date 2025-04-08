package ru.T1Academy.FirstProject.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.T1Academy.FirstProject.dto.event.TaskStatusChangeEvent;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskStatusChangeConsumer {

    @KafkaListener(id = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.task-status-update-topic}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload List<TaskStatusChangeEvent> taskStatusChangeEventList,
                       Acknowledgment ack) {

        log.info("Читаю размер {}.", taskStatusChangeEventList.size());
        log.info("Читаю данные {}.", taskStatusChangeEventList.get(0));

        ack.acknowledge();
    }
}