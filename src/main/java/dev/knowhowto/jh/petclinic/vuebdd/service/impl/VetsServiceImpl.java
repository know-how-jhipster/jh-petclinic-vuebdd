package dev.knowhowto.jh.petclinic.vuebdd.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Vets;
import dev.knowhowto.jh.petclinic.vuebdd.repository.VetsRepository;
import dev.knowhowto.jh.petclinic.vuebdd.repository.search.VetsSearchRepository;
import dev.knowhowto.jh.petclinic.vuebdd.service.VetsService;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.VetsDTO;
import dev.knowhowto.jh.petclinic.vuebdd.service.mapper.VetsMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Vets}.
 */
@Service
@Transactional
public class VetsServiceImpl implements VetsService {

    private final Logger log = LoggerFactory.getLogger(VetsServiceImpl.class);

    private final VetsRepository vetsRepository;

    private final VetsMapper vetsMapper;

    private final VetsSearchRepository vetsSearchRepository;

    public VetsServiceImpl(VetsRepository vetsRepository, VetsMapper vetsMapper, VetsSearchRepository vetsSearchRepository) {
        this.vetsRepository = vetsRepository;
        this.vetsMapper = vetsMapper;
        this.vetsSearchRepository = vetsSearchRepository;
    }

    @Override
    public Mono<VetsDTO> save(VetsDTO vetsDTO) {
        log.debug("Request to save Vets : {}", vetsDTO);
        return vetsRepository.save(vetsMapper.toEntity(vetsDTO)).flatMap(vetsSearchRepository::save).map(vetsMapper::toDto);
    }

    @Override
    public Mono<VetsDTO> update(VetsDTO vetsDTO) {
        log.debug("Request to update Vets : {}", vetsDTO);
        return vetsRepository.save(vetsMapper.toEntity(vetsDTO)).flatMap(vetsSearchRepository::save).map(vetsMapper::toDto);
    }

    @Override
    public Mono<VetsDTO> partialUpdate(VetsDTO vetsDTO) {
        log.debug("Request to partially update Vets : {}", vetsDTO);

        return vetsRepository
            .findById(vetsDTO.getId())
            .map(existingVets -> {
                vetsMapper.partialUpdate(existingVets, vetsDTO);

                return existingVets;
            })
            .flatMap(vetsRepository::save)
            .flatMap(savedVets -> {
                vetsSearchRepository.save(savedVets);

                return Mono.just(savedVets);
            })
            .map(vetsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<VetsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Vets");
        return vetsRepository.findAllBy(pageable).map(vetsMapper::toDto);
    }

    public Mono<Long> countAll() {
        return vetsRepository.count();
    }

    public Mono<Long> searchCount() {
        return vetsSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<VetsDTO> findOne(Long id) {
        log.debug("Request to get Vets : {}", id);
        return vetsRepository.findById(id).map(vetsMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Vets : {}", id);
        return vetsRepository.deleteById(id).then(vetsSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<VetsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Vets for query {}", query);
        return vetsSearchRepository.search(query, pageable).map(vetsMapper::toDto);
    }
}
