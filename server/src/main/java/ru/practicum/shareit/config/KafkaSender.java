package ru.practicum.shareit.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSender {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String text, String topicName) {
        kafkaTemplate.send(topicName, text);

        log.info("News produced {}", text);

    }
}
