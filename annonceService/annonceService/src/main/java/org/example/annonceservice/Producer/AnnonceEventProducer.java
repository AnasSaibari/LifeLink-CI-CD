package org.example.annonceservice.Producer;

import lombok.RequiredArgsConstructor;
import org.example.annonceservice.DTO.AnnonceDTO;
import org.example.annonceservice.Event.AnnonceCreatedEvent;
import org.example.annonceservice.Event.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnnonceEventProducer {

    private final KafkaTemplate<String, AnnonceCreatedEvent> kafkaTemplate;

    public void publishAnnonceCreated(AnnonceDTO annonceDTO) {
        kafkaTemplate.send(
                KafkaTopics.ANNONCE_CREATED,
                new AnnonceCreatedEvent(annonceDTO)
        );
    }
}
