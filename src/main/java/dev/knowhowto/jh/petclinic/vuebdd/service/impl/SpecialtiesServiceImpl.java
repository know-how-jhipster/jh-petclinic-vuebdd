package dev.knowhowto.jh.petclinic.vuebdd.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Specialties;
import dev.knowhowto.jh.petclinic.vuebdd.repository.SpecialtiesRepository;
import dev.knowhowto.jh.petclinic.vuebdd.repository.search.SpecialtiesSearchRepository;
import dev.knowhowto.jh.petclinic.vuebdd.service.SpecialtiesService;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.SpecialtiesDTO;
import dev.knowhowto.jh.petclinic.vuebdd.service.mapper.SpecialtiesMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Specialties}.
 */
@Service
@Transactional
public class SpecialtiesServiceImpl implements SpecialtiesService {

    private final Logger log = LoggerFactory.getLogger(SpecialtiesServiceImpl.class);

    private final SpecialtiesRepository specialtiesRepository;

    private final SpecialtiesMapper specialtiesMapper;

    private final SpecialtiesSearchRepository specialtiesSearchRepository;

    public SpecialtiesServiceImpl(
        SpecialtiesRepository specialtiesRepository,
        SpecialtiesMapper specialtiesMapper,
        SpecialtiesSearchRepository specialtiesSearchRepository
    ) {
        this.specialtiesRepository = specialtiesRepository;
        this.specialtiesMapper = specialtiesMapper;
        this.specialtiesSearchRepository = specialtiesSearchRepository;
    }

    @Override
    public Mono<SpecialtiesDTO> save(SpecialtiesDTO specialtiesDTO) {
        log.debug("Request to save Specialties : {}", specialtiesDTO);
        return specialtiesRepository
            .save(specialtiesMapper.toEntity(specialtiesDTO))
            .flatMap(specialtiesSearchRepository::save)
            .map(specialtiesMapper::toDto);
    }

    @Override
    public Mono<SpecialtiesDTO> update(SpecialtiesDTO specialtiesDTO) {
        log.debug("Request to update Specialties : {}", specialtiesDTO);
        return specialtiesRepository
            .save(specialtiesMapper.toEntity(specialtiesDTO))
            .flatMap(specialtiesSearchRepository::save)
            .map(specialtiesMapper::toDto);
    }

    @Override
    public Mono<SpecialtiesDTO> partialUpdate(SpecialtiesDTO specialtiesDTO) {
        log.debug("Request to partially update Specialties : {}", specialtiesDTO);

        return specialtiesRepository
            .findById(specialtiesDTO.getId())
            .map(existingSpecialties -> {
                specialtiesMapper.partialUpdate(existingSpecialties, specialtiesDTO);

                return existingSpecialties;
            })
            .flatMap(specialtiesRepository::save)
            .flatMap(savedSpecialties -> {
                specialtiesSearchRepository.save(savedSpecialties);

                return Mono.just(savedSpecialties);
            })
            .map(specialtiesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SpecialtiesDTO> findAll() {
        log.debug("Request to get all Specialties");
        return specialtiesRepository.findAll().map(specialtiesMapper::toDto);
    }

    public Flux<SpecialtiesDTO> findAllWithEagerRelationships(Pageable pageable) {
        return specialtiesRepository.findAllWithEagerRelationships(pageable).map(specialtiesMapper::toDto);
    }

    public Mono<Long> countAll() {
        return specialtiesRepository.count();
    }

    public Mono<Long> searchCount() {
        return specialtiesSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SpecialtiesDTO> findOne(Long id) {
        log.debug("Request to get Specialties : {}", id);
        return specialtiesRepository.findOneWithEagerRelationships(id).map(specialtiesMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Specialties : {}", id);
        return specialtiesRepository.deleteById(id).then(specialtiesSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SpecialtiesDTO> search(String query) {
        log.debug("Request to search Specialties for query {}", query);
        return specialtiesSearchRepository.search(query).map(specialtiesMapper::toDto);
    }
}
