package org.example.annonceservice.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.annonceservice.DTO.AnnonceDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceCreatedEvent {
    AnnonceDTO annonce;
}
