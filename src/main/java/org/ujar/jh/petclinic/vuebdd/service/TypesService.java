package org.ujar.jh.petclinic.vuebdd.service;

import org.springframework.data.domain.Pageable;
import org.ujar.jh.petclinic.vuebdd.service.dto.TypesDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.ujar.jh.petclinic.vuebdd.domain.Types}.
 */
public interface TypesService {
    /**
     * Save a types.
     *
     * @param typesDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TypesDTO> save(TypesDTO typesDTO);

    /**
     * Updates a types.
     *
     * @param typesDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TypesDTO> update(TypesDTO typesDTO);

    /**
     * Partially updates a types.
     *
     * @param typesDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TypesDTO> partialUpdate(TypesDTO typesDTO);

    /**
     * Get all the types.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TypesDTO> findAll(Pageable pageable);

    /**
     * Returns the number of types available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of types available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" types.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TypesDTO> findOne(Long id);

    /**
     * Delete the "id" types.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the types corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TypesDTO> search(String query, Pageable pageable);
}
