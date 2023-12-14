package dev.knowhowto.jh.petclinic.vuebdd.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.SpecialtiesDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link dev.knowhowto.jh.petclinic.vuebdd.domain.Specialties}.
 */
public interface SpecialtiesService {
    /**
     * Save a specialties.
     *
     * @param specialtiesDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SpecialtiesDTO> save(SpecialtiesDTO specialtiesDTO);

    /**
     * Updates a specialties.
     *
     * @param specialtiesDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SpecialtiesDTO> update(SpecialtiesDTO specialtiesDTO);

    /**
     * Partially updates a specialties.
     *
     * @param specialtiesDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SpecialtiesDTO> partialUpdate(SpecialtiesDTO specialtiesDTO);

    /**
     * Get all the specialties.
     *
     * @return the list of entities.
     */
    Flux<SpecialtiesDTO> findAll();

    /**
     * Get all the specialties with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SpecialtiesDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of specialties available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of specialties available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" specialties.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SpecialtiesDTO> findOne(Long id);

    /**
     * Delete the "id" specialties.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the specialties corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<SpecialtiesDTO> search(String query);
}
