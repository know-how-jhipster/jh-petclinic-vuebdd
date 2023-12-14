package dev.knowhowto.jh.petclinic.vuebdd.service;

import org.springframework.data.domain.Pageable;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.VisitsDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link dev.knowhowto.jh.petclinic.vuebdd.domain.Visits}.
 */
public interface VisitsService {
    /**
     * Save a visits.
     *
     * @param visitsDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<VisitsDTO> save(VisitsDTO visitsDTO);

    /**
     * Updates a visits.
     *
     * @param visitsDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<VisitsDTO> update(VisitsDTO visitsDTO);

    /**
     * Partially updates a visits.
     *
     * @param visitsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<VisitsDTO> partialUpdate(VisitsDTO visitsDTO);

    /**
     * Get all the visits.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<VisitsDTO> findAll(Pageable pageable);

    /**
     * Returns the number of visits available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of visits available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" visits.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<VisitsDTO> findOne(Long id);

    /**
     * Delete the "id" visits.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the visits corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<VisitsDTO> search(String query, Pageable pageable);
}
