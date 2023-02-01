package org.ujar.jh.petclinic.vuebdd.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ujar.jh.petclinic.vuebdd.domain.Visits;
import org.ujar.jh.petclinic.vuebdd.repository.VisitsRepository;
import org.ujar.jh.petclinic.vuebdd.repository.search.VisitsSearchRepository;
import org.ujar.jh.petclinic.vuebdd.service.VisitsService;
import org.ujar.jh.petclinic.vuebdd.service.dto.VisitsDTO;
import org.ujar.jh.petclinic.vuebdd.service.mapper.VisitsMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Visits}.
 */
@Service
@Transactional
public class VisitsServiceImpl implements VisitsService {

    private final Logger log = LoggerFactory.getLogger(VisitsServiceImpl.class);

    private final VisitsRepository visitsRepository;

    private final VisitsMapper visitsMapper;

    private final VisitsSearchRepository visitsSearchRepository;

    public VisitsServiceImpl(VisitsRepository visitsRepository, VisitsMapper visitsMapper, VisitsSearchRepository visitsSearchRepository) {
        this.visitsRepository = visitsRepository;
        this.visitsMapper = visitsMapper;
        this.visitsSearchRepository = visitsSearchRepository;
    }

    @Override
    public Mono<VisitsDTO> save(VisitsDTO visitsDTO) {
        log.debug("Request to save Visits : {}", visitsDTO);
        return visitsRepository.save(visitsMapper.toEntity(visitsDTO)).flatMap(visitsSearchRepository::save).map(visitsMapper::toDto);
    }

    @Override
    public Mono<VisitsDTO> update(VisitsDTO visitsDTO) {
        log.debug("Request to update Visits : {}", visitsDTO);
        return visitsRepository.save(visitsMapper.toEntity(visitsDTO)).flatMap(visitsSearchRepository::save).map(visitsMapper::toDto);
    }

    @Override
    public Mono<VisitsDTO> partialUpdate(VisitsDTO visitsDTO) {
        log.debug("Request to partially update Visits : {}", visitsDTO);

        return visitsRepository
            .findById(visitsDTO.getId())
            .map(existingVisits -> {
                visitsMapper.partialUpdate(existingVisits, visitsDTO);

                return existingVisits;
            })
            .flatMap(visitsRepository::save)
            .flatMap(savedVisits -> {
                visitsSearchRepository.save(savedVisits);

                return Mono.just(savedVisits);
            })
            .map(visitsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<VisitsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Visits");
        return visitsRepository.findAllBy(pageable).map(visitsMapper::toDto);
    }

    public Mono<Long> countAll() {
        return visitsRepository.count();
    }

    public Mono<Long> searchCount() {
        return visitsSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<VisitsDTO> findOne(Long id) {
        log.debug("Request to get Visits : {}", id);
        return visitsRepository.findById(id).map(visitsMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Visits : {}", id);
        return visitsRepository.deleteById(id).then(visitsSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<VisitsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Visits for query {}", query);
        return visitsSearchRepository.search(query, pageable).map(visitsMapper::toDto);
    }
}
