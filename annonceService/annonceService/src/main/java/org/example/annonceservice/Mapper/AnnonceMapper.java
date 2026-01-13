package org.example.annonceservice.Mapper;

import org.example.annonceservice.DTO.AnnonceDTO;
import org.example.annonceservice.Entity.Annonce;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AnnonceMapper {

    AnnonceDTO toDto(Annonce annonce);

    Annonce toEntity(AnnonceDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Annonce updateAnnonceFromDto(AnnonceDTO dto, @MappingTarget Annonce annonce);
}

