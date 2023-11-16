package dev.knowhowto.jh.petclinic.vuebdd.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import dev.knowhowto.jh.petclinic.vuebdd.repository.SpecialtiesRepository;
import dev.knowhowto.jh.petclinic.vuebdd.service.SpecialtiesService;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.SpecialtiesDTO;
import dev.knowhowto.jh.petclinic.vuebdd.web.rest.errors.BadRequestAlertException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link dev.knowhowto.jh.petclinic.vuebdd.domain.Specialties}.
 */
@RestController
@RequestMapping("/api")
public class SpecialtiesResource {

    private final Logger log = LoggerFactory.getLogger(SpecialtiesResource.class);

    private static final String ENTITY_NAME = "specialties";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpecialtiesService specialtiesService;

    private final SpecialtiesRepository specialtiesRepository;

    public SpecialtiesResource(SpecialtiesService specialtiesService, SpecialtiesRepository specialtiesRepository) {
        this.specialtiesService = specialtiesService;
        this.specialtiesRepository = specialtiesRepository;
    }

    /**
     * {@code POST  /specialties} : Create a new specialties.
     *
     * @param specialtiesDTO the specialtiesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new specialtiesDTO, or with status {@code 400 (Bad Request)} if the specialties has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/specialties")
    public Mono<ResponseEntity<SpecialtiesDTO>> createSpecialties(@Valid @RequestBody SpecialtiesDTO specialtiesDTO)
        throws URISyntaxException {
        log.debug("REST request to save Specialties : {}", specialtiesDTO);
        if (specialtiesDTO.getId() != null) {
            throw new BadRequestAlertException("A new specialties cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return specialtiesService
            .save(specialtiesDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/specialties/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /specialties/:id} : Updates an existing specialties.
     *
     * @param id the id of the specialtiesDTO to save.
     * @param specialtiesDTO the specialtiesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated specialtiesDTO,
     * or with status {@code 400 (Bad Request)} if the specialtiesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the specialtiesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/specialties/{id}")
    public Mono<ResponseEntity<SpecialtiesDTO>> updateSpecialties(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SpecialtiesDTO specialtiesDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Specialties : {}, {}", id, specialtiesDTO);
        if (specialtiesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, specialtiesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return specialtiesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return specialtiesService
                    .update(specialtiesDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /specialties/:id} : Partial updates given fields of an existing specialties, field will ignore if it is null
     *
     * @param id the id of the specialtiesDTO to save.
     * @param specialtiesDTO the specialtiesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated specialtiesDTO,
     * or with status {@code 400 (Bad Request)} if the specialtiesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the specialtiesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the specialtiesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/specialties/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SpecialtiesDTO>> partialUpdateSpecialties(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SpecialtiesDTO specialtiesDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Specialties partially : {}, {}", id, specialtiesDTO);
        if (specialtiesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, specialtiesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return specialtiesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SpecialtiesDTO> result = specialtiesService.partialUpdate(specialtiesDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /specialties} : get all the specialties.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of specialties in body.
     */
    @GetMapping("/specialties")
    public Mono<List<SpecialtiesDTO>> getAllSpecialties(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Specialties");
        return specialtiesService.findAll().collectList();
    }

    /**
     * {@code GET  /specialties} : get all the specialties as a stream.
     * @return the {@link Flux} of specialties.
     */
    @GetMapping(value = "/specialties", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SpecialtiesDTO> getAllSpecialtiesAsStream() {
        log.debug("REST request to get all Specialties as a stream");
        return specialtiesService.findAll();
    }

    /**
     * {@code GET  /specialties/:id} : get the "id" specialties.
     *
     * @param id the id of the specialtiesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the specialtiesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/specialties/{id}")
    public Mono<ResponseEntity<SpecialtiesDTO>> getSpecialties(@PathVariable Long id) {
        log.debug("REST request to get Specialties : {}", id);
        Mono<SpecialtiesDTO> specialtiesDTO = specialtiesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(specialtiesDTO);
    }

    /**
     * {@code DELETE  /specialties/:id} : delete the "id" specialties.
     *
     * @param id the id of the specialtiesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/specialties/{id}")
    public Mono<ResponseEntity<Void>> deleteSpecialties(@PathVariable Long id) {
        log.debug("REST request to delete Specialties : {}", id);
        return specialtiesService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /_search/specialties?query=:query} : search for the specialties corresponding
     * to the query.
     *
     * @param query the query of the specialties search.
     * @return the result of the search.
     */
    @GetMapping("/_search/specialties")
    public Mono<List<SpecialtiesDTO>> searchSpecialties(@RequestParam String query) {
        log.debug("REST request to search Specialties for query {}", query);
        return specialtiesService.search(query).collectList();
    }
}
