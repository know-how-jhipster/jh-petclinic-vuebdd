package org.ujar.jh.petclinic.vuebdd.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ujar.jh.petclinic.vuebdd.domain.Types;
import org.ujar.jh.petclinic.vuebdd.repository.TypesRepository;
import org.ujar.jh.petclinic.vuebdd.repository.search.TypesSearchRepository;
import org.ujar.jh.petclinic.vuebdd.service.TypesService;
import org.ujar.jh.petclinic.vuebdd.service.dto.TypesDTO;
import org.ujar.jh.petclinic.vuebdd.service.mapper.TypesMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Types}.
 */
@Service
@Transactional
public class TypesServiceImpl implements TypesService {

    private final Logger log = LoggerFactory.getLogger(TypesServiceImpl.class);

    private final TypesRepository typesRepository;

    private final TypesMapper typesMapper;

    private final TypesSearchRepository typesSearchRepository;

    public TypesServiceImpl(TypesRepository typesRepository, TypesMapper typesMapper, TypesSearchRepository typesSearchRepository) {
        this.typesRepository = typesRepository;
        this.typesMapper = typesMapper;
        this.typesSearchRepository = typesSearchRepository;
    }

    @Override
    public Mono<TypesDTO> save(TypesDTO typesDTO) {
        log.debug("Request to save Types : {}", typesDTO);
        return typesRepository.save(typesMapper.toEntity(typesDTO)).flatMap(typesSearchRepository::save).map(typesMapper::toDto);
    }

    @Override
    public Mono<TypesDTO> update(TypesDTO typesDTO) {
        log.debug("Request to update Types : {}", typesDTO);
        return typesRepository.save(typesMapper.toEntity(typesDTO)).flatMap(typesSearchRepository::save).map(typesMapper::toDto);
    }

    @Override
    public Mono<TypesDTO> partialUpdate(TypesDTO typesDTO) {
        log.debug("Request to partially update Types : {}", typesDTO);

        return typesRepository
            .findById(typesDTO.getId())
            .map(existingTypes -> {
                typesMapper.partialUpdate(existingTypes, typesDTO);

                return existingTypes;
            })
            .flatMap(typesRepository::save)
            .flatMap(savedTypes -> {
                typesSearchRepository.save(savedTypes);

                return Mono.just(savedTypes);
            })
            .map(typesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TypesDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Types");
        return typesRepository.findAllBy(pageable).map(typesMapper::toDto);
    }

    public Mono<Long> countAll() {
        return typesRepository.count();
    }

    public Mono<Long> searchCount() {
        return typesSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TypesDTO> findOne(Long id) {
        log.debug("Request to get Types : {}", id);
        return typesRepository.findById(id).map(typesMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Types : {}", id);
        return typesRepository.deleteById(id).then(typesSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TypesDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Types for query {}", query);
        return typesSearchRepository.search(query, pageable).map(typesMapper::toDto);
    }
}
