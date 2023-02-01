package org.ujar.jh.petclinic.vuebdd.service;

import org.springframework.data.domain.Pageable;
import org.ujar.jh.petclinic.vuebdd.service.dto.PetsDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.ujar.jh.petclinic.vuebdd.domain.Pets}.
 */
public interface PetsService {
    /**
     * Save a pets.
     *
     * @param petsDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PetsDTO> save(PetsDTO petsDTO);

    /**
     * Updates a pets.
     *
     * @param petsDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PetsDTO> update(PetsDTO petsDTO);

    /**
     * Partially updates a pets.
     *
     * @param petsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PetsDTO> partialUpdate(PetsDTO petsDTO);

    /**
     * Get all the pets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PetsDTO> findAll(Pageable pageable);

    /**
     * Returns the number of pets available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of pets available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" pets.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PetsDTO> findOne(Long id);

    /**
     * Delete the "id" pets.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the pets corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PetsDTO> search(String query, Pageable pageable);
}
