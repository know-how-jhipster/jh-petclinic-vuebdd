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
import org.ujar.jh.petclinic.vuebdd.repository.OwnersRepository;
import org.ujar.jh.petclinic.vuebdd.service.OwnersService;
import org.ujar.jh.petclinic.vuebdd.service.dto.OwnersDTO;
import org.ujar.jh.petclinic.vuebdd.web.rest.errors.BadRequestAlertException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link org.ujar.jh.petclinic.vuebdd.domain.Owners}.
 */
@RestController
@RequestMapping("/api")
public class OwnersResource {

    private final Logger log = LoggerFactory.getLogger(OwnersResource.class);

    private static final String ENTITY_NAME = "owners";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OwnersService ownersService;

    private final OwnersRepository ownersRepository;

    public OwnersResource(OwnersService ownersService, OwnersRepository ownersRepository) {
        this.ownersService = ownersService;
        this.ownersRepository = ownersRepository;
    }

    /**
     * {@code POST  /owners} : Create a new owners.
     *
     * @param ownersDTO the ownersDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ownersDTO, or with status {@code 400 (Bad Request)} if the owners has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/owners")
    public Mono<ResponseEntity<OwnersDTO>> createOwners(@Valid @RequestBody OwnersDTO ownersDTO) throws URISyntaxException {
        log.debug("REST request to save Owners : {}", ownersDTO);
        if (ownersDTO.getId() != null) {
            throw new BadRequestAlertException("A new owners cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return ownersService
            .save(ownersDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/owners/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /owners/:id} : Updates an existing owners.
     *
     * @param id the id of the ownersDTO to save.
     * @param ownersDTO the ownersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ownersDTO,
     * or with status {@code 400 (Bad Request)} if the ownersDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ownersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/owners/{id}")
    public Mono<ResponseEntity<OwnersDTO>> updateOwners(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OwnersDTO ownersDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Owners : {}, {}", id, ownersDTO);
        if (ownersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ownersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ownersRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return ownersService
                    .update(ownersDTO)
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
     * {@code PATCH  /owners/:id} : Partial updates given fields of an existing owners, field will ignore if it is null
     *
     * @param id the id of the ownersDTO to save.
     * @param ownersDTO the ownersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ownersDTO,
     * or with status {@code 400 (Bad Request)} if the ownersDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ownersDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ownersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/owners/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OwnersDTO>> partialUpdateOwners(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OwnersDTO ownersDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Owners partially : {}, {}", id, ownersDTO);
        if (ownersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ownersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ownersRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OwnersDTO> result = ownersService.partialUpdate(ownersDTO);

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
     * {@code GET  /owners} : get all the owners.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of owners in body.
     */
    @GetMapping("/owners")
    public Mono<ResponseEntity<List<OwnersDTO>>> getAllOwners(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Owners");
        return ownersService
            .countAll()
            .zipWith(ownersService.findAll(pageable).collectList())
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
     * {@code GET  /owners/:id} : get the "id" owners.
     *
     * @param id the id of the ownersDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ownersDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/owners/{id}")
    public Mono<ResponseEntity<OwnersDTO>> getOwners(@PathVariable Long id) {
        log.debug("REST request to get Owners : {}", id);
        Mono<OwnersDTO> ownersDTO = ownersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ownersDTO);
    }

    /**
     * {@code DELETE  /owners/:id} : delete the "id" owners.
     *
     * @param id the id of the ownersDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/owners/{id}")
    public Mono<ResponseEntity<Void>> deleteOwners(@PathVariable Long id) {
        log.debug("REST request to delete Owners : {}", id);
        return ownersService
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
     * {@code SEARCH  /_search/owners?query=:query} : search for the owners corresponding
     * to the query.
     *
     * @param query the query of the owners search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/owners")
    public Mono<ResponseEntity<Flux<OwnersDTO>>> searchOwners(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Owners for query {}", query);
        return ownersService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(ownersService.search(query, pageable)));
    }
}
