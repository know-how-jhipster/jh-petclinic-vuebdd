package dev.knowhowto.jh.petclinic.vuebdd.service.mapper;

import org.mapstruct.*;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Types;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.TypesDTO;

/**
 * Mapper for the entity {@link Types} and its DTO {@link TypesDTO}.
 */
@Mapper(componentModel = "spring")
public interface TypesMapper extends EntityMapper<TypesDTO, Types> {}
