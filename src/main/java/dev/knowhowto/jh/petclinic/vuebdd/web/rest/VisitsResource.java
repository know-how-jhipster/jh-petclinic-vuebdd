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
import dev.knowhowto.jh.petclinic.vuebdd.repository.VisitsRepository;
import dev.knowhowto.jh.petclinic.vuebdd.service.VisitsService;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.VisitsDTO;
import dev.knowhowto.jh.petclinic.vuebdd.web.rest.errors.BadRequestAlertException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link dev.knowhowto.jh.petclinic.vuebdd.domain.Visits}.
 */
@RestController
@RequestMapping("/api")
public class VisitsResource {

    private final Logger log = LoggerFactory.getLogger(VisitsResource.class);

    private static final String ENTITY_NAME = "visits";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VisitsService visitsService;

    private final VisitsRepository visitsRepository;

    public VisitsResource(VisitsService visitsService, VisitsRepository visitsRepository) {
        this.visitsService = visitsService;
        this.visitsRepository = visitsRepository;
    }

    /**
     * {@code POST  /visits} : Create a new visits.
     *
     * @param visitsDTO the visitsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new visitsDTO, or with status {@code 400 (Bad Request)} if the visits has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/visits")
    public Mono<ResponseEntity<VisitsDTO>> createVisits(@Valid @RequestBody VisitsDTO visitsDTO) throws URISyntaxException {
        log.debug("REST request to save Visits : {}", visitsDTO);
        if (visitsDTO.getId() != null) {
            throw new BadRequestAlertException("A new visits cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return visitsService
            .save(visitsDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/visits/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /visits/:id} : Updates an existing visits.
     *
     * @param id the id of the visitsDTO to save.
     * @param visitsDTO the visitsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitsDTO,
     * or with status {@code 400 (Bad Request)} if the visitsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the visitsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/visits/{id}")
    public Mono<ResponseEntity<VisitsDTO>> updateVisits(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VisitsDTO visitsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Visits : {}, {}", id, visitsDTO);
        if (visitsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return visitsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return visitsService
                    .update(visitsDTO)
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
     * {@code PATCH  /visits/:id} : Partial updates given fields of an existing visits, field will ignore if it is null
     *
     * @param id the id of the visitsDTO to save.
     * @param visitsDTO the visitsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitsDTO,
     * or with status {@code 400 (Bad Request)} if the visitsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the visitsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the visitsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/visits/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<VisitsDTO>> partialUpdateVisits(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VisitsDTO visitsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Visits partially : {}, {}", id, visitsDTO);
        if (visitsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return visitsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<VisitsDTO> result = visitsService.partialUpdate(visitsDTO);

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
     * {@code GET  /visits} : get all the visits.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of visits in body.
     */
    @GetMapping("/visits")
    public Mono<ResponseEntity<List<VisitsDTO>>> getAllVisits(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Visits");
        return visitsService
            .countAll()
            .zipWith(visitsService.findAll(pageable).collectList())
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
     * {@code GET  /visits/:id} : get the "id" visits.
     *
     * @param id the id of the visitsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the visitsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/visits/{id}")
    public Mono<ResponseEntity<VisitsDTO>> getVisits(@PathVariable Long id) {
        log.debug("REST request to get Visits : {}", id);
        Mono<VisitsDTO> visitsDTO = visitsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(visitsDTO);
    }

    /**
     * {@code DELETE  /visits/:id} : delete the "id" visits.
     *
     * @param id the id of the visitsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/visits/{id}")
    public Mono<ResponseEntity<Void>> deleteVisits(@PathVariable Long id) {
        log.debug("REST request to delete Visits : {}", id);
        return visitsService
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
     * {@code SEARCH  /_search/visits?query=:query} : search for the visits corresponding
     * to the query.
     *
     * @param query the query of the visits search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/visits")
    public Mono<ResponseEntity<Flux<VisitsDTO>>> searchVisits(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Visits for query {}", query);
        return visitsService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(visitsService.search(query, pageable)));
    }
}
