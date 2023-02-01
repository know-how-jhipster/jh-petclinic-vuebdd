package org.ujar.jh.petclinic.vuebdd.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import org.ujar.jh.petclinic.vuebdd.repository.PetsRepository;
import org.ujar.jh.petclinic.vuebdd.service.PetsService;
import org.ujar.jh.petclinic.vuebdd.service.dto.PetsDTO;
import org.ujar.jh.petclinic.vuebdd.web.rest.errors.BadRequestAlertException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link org.ujar.jh.petclinic.vuebdd.domain.Pets}.
 */
@RestController
@RequestMapping("/api")
public class PetsResource {

    private final Logger log = LoggerFactory.getLogger(PetsResource.class);

    private static final String ENTITY_NAME = "pets";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PetsService petsService;

    private final PetsRepository petsRepository;

    public PetsResource(PetsService petsService, PetsRepository petsRepository) {
        this.petsService = petsService;
        this.petsRepository = petsRepository;
    }

    /**
     * {@code POST  /pets} : Create a new pets.
     *
     * @param petsDTO the petsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new petsDTO, or with status {@code 400 (Bad Request)} if the pets has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pets")
    public Mono<ResponseEntity<PetsDTO>> createPets(@Valid @RequestBody PetsDTO petsDTO) throws URISyntaxException {
        log.debug("REST request to save Pets : {}", petsDTO);
        if (petsDTO.getId() != null) {
            throw new BadRequestAlertException("A new pets cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return petsService
            .save(petsDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/pets/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /pets/:id} : Updates an existing pets.
     *
     * @param id the id of the petsDTO to save.
     * @param petsDTO the petsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated petsDTO,
     * or with status {@code 400 (Bad Request)} if the petsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the petsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pets/{id}")
    public Mono<ResponseEntity<PetsDTO>> updatePets(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PetsDTO petsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Pets : {}, {}", id, petsDTO);
        if (petsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, petsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return petsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return petsService
                    .update(petsDTO)
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
     * {@code PATCH  /pets/:id} : Partial updates given fields of an existing pets, field will ignore if it is null
     *
     * @param id the id of the petsDTO to save.
     * @param petsDTO the petsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated petsDTO,
     * or with status {@code 400 (Bad Request)} if the petsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the petsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the petsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PetsDTO>> partialUpdatePets(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PetsDTO petsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pets partially : {}, {}", id, petsDTO);
        if (petsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, petsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return petsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PetsDTO> result = petsService.partialUpdate(petsDTO);

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
     * {@code GET  /pets} : get all the pets.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pets in body.
     */
    @GetMapping("/pets")
    public Mono<ResponseEntity<List<PetsDTO>>> getAllPets(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Pets");
        return petsService
            .countAll()
            .zipWith(petsService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /pets/:id} : get the "id" pets.
     *
     * @param id the id of the petsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the petsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pets/{id}")
    public Mono<ResponseEntity<PetsDTO>> getPets(@PathVariable Long id) {
        log.debug("REST request to get Pets : {}", id);
        Mono<PetsDTO> petsDTO = petsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(petsDTO);
    }

    /**
     * {@code DELETE  /pets/:id} : delete the "id" pets.
     *
     * @param id the id of the petsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pets/{id}")
    public Mono<ResponseEntity<Void>> deletePets(@PathVariable Long id) {
        log.debug("REST request to delete Pets : {}", id);
        return petsService
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
     * {@code SEARCH  /_search/pets?query=:query} : search for the pets corresponding
     * to the query.
     *
     * @param query the query of the pets search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/pets")
    public Mono<ResponseEntity<Flux<PetsDTO>>> searchPets(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Pets for query {}", query);
        return petsService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(petsService.search(query, pageable)));
    }
}
