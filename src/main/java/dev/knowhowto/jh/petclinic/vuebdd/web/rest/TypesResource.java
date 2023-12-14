package dev.knowhowto.jh.petclinic.vuebdd.web.rest;

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
import dev.knowhowto.jh.petclinic.vuebdd.repository.TypesRepository;
import dev.knowhowto.jh.petclinic.vuebdd.service.TypesService;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.TypesDTO;
import dev.knowhowto.jh.petclinic.vuebdd.web.rest.errors.BadRequestAlertException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link dev.knowhowto.jh.petclinic.vuebdd.domain.Types}.
 */
@RestController
@RequestMapping("/api")
public class TypesResource {

    private final Logger log = LoggerFactory.getLogger(TypesResource.class);

    private static final String ENTITY_NAME = "types";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TypesService typesService;

    private final TypesRepository typesRepository;

    public TypesResource(TypesService typesService, TypesRepository typesRepository) {
        this.typesService = typesService;
        this.typesRepository = typesRepository;
    }

    /**
     * {@code POST  /types} : Create a new types.
     *
     * @param typesDTO the typesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new typesDTO, or with status {@code 400 (Bad Request)} if the types has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/types")
    public Mono<ResponseEntity<TypesDTO>> createTypes(@Valid @RequestBody TypesDTO typesDTO) throws URISyntaxException {
        log.debug("REST request to save Types : {}", typesDTO);
        if (typesDTO.getId() != null) {
            throw new BadRequestAlertException("A new types cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return typesService
            .save(typesDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/types/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /types/:id} : Updates an existing types.
     *
     * @param id the id of the typesDTO to save.
     * @param typesDTO the typesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typesDTO,
     * or with status {@code 400 (Bad Request)} if the typesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the typesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/types/{id}")
    public Mono<ResponseEntity<TypesDTO>> updateTypes(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TypesDTO typesDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Types : {}, {}", id, typesDTO);
        if (typesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return typesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return typesService
                    .update(typesDTO)
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
     * {@code PATCH  /types/:id} : Partial updates given fields of an existing types, field will ignore if it is null
     *
     * @param id the id of the typesDTO to save.
     * @param typesDTO the typesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typesDTO,
     * or with status {@code 400 (Bad Request)} if the typesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the typesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the typesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TypesDTO>> partialUpdateTypes(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TypesDTO typesDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Types partially : {}, {}", id, typesDTO);
        if (typesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return typesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TypesDTO> result = typesService.partialUpdate(typesDTO);

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
     * {@code GET  /types} : get all the types.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of types in body.
     */
    @GetMapping("/types")
    public Mono<ResponseEntity<List<TypesDTO>>> getAllTypes(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Types");
        return typesService
            .countAll()
            .zipWith(typesService.findAll(pageable).collectList())
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
     * {@code GET  /types/:id} : get the "id" types.
     *
     * @param id the id of the typesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the typesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/types/{id}")
    public Mono<ResponseEntity<TypesDTO>> getTypes(@PathVariable Long id) {
        log.debug("REST request to get Types : {}", id);
        Mono<TypesDTO> typesDTO = typesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(typesDTO);
    }

    /**
     * {@code DELETE  /types/:id} : delete the "id" types.
     *
     * @param id the id of the typesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/types/{id}")
    public Mono<ResponseEntity<Void>> deleteTypes(@PathVariable Long id) {
        log.debug("REST request to delete Types : {}", id);
        return typesService
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
     * {@code SEARCH  /_search/types?query=:query} : search for the types corresponding
     * to the query.
     *
     * @param query the query of the types search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/types")
    public Mono<ResponseEntity<Flux<TypesDTO>>> searchTypes(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Types for query {}", query);
        return typesService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(typesService.search(query, pageable)));
    }
}
