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
import dev.knowhowto.jh.petclinic.vuebdd.repository.VetsRepository;
import dev.knowhowto.jh.petclinic.vuebdd.service.VetsService;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.VetsDTO;
import dev.knowhowto.jh.petclinic.vuebdd.web.rest.errors.BadRequestAlertException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link dev.knowhowto.jh.petclinic.vuebdd.domain.Vets}.
 */
@RestController
@RequestMapping("/api")
public class VetsResource {

    private final Logger log = LoggerFactory.getLogger(VetsResource.class);

    private static final String ENTITY_NAME = "vets";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VetsService vetsService;

    private final VetsRepository vetsRepository;

    public VetsResource(VetsService vetsService, VetsRepository vetsRepository) {
        this.vetsService = vetsService;
        this.vetsRepository = vetsRepository;
    }

    /**
     * {@code POST  /vets} : Create a new vets.
     *
     * @param vetsDTO the vetsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vetsDTO, or with status {@code 400 (Bad Request)} if the vets has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vets")
    public Mono<ResponseEntity<VetsDTO>> createVets(@Valid @RequestBody VetsDTO vetsDTO) throws URISyntaxException {
        log.debug("REST request to save Vets : {}", vetsDTO);
        if (vetsDTO.getId() != null) {
            throw new BadRequestAlertException("A new vets cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return vetsService
            .save(vetsDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/vets/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /vets/:id} : Updates an existing vets.
     *
     * @param id the id of the vetsDTO to save.
     * @param vetsDTO the vetsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vetsDTO,
     * or with status {@code 400 (Bad Request)} if the vetsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vetsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/vets/{id}")
    public Mono<ResponseEntity<VetsDTO>> updateVets(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VetsDTO vetsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Vets : {}, {}", id, vetsDTO);
        if (vetsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vetsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return vetsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return vetsService
                    .update(vetsDTO)
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
     * {@code PATCH  /vets/:id} : Partial updates given fields of an existing vets, field will ignore if it is null
     *
     * @param id the id of the vetsDTO to save.
     * @param vetsDTO the vetsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vetsDTO,
     * or with status {@code 400 (Bad Request)} if the vetsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the vetsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the vetsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/vets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<VetsDTO>> partialUpdateVets(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VetsDTO vetsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Vets partially : {}, {}", id, vetsDTO);
        if (vetsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vetsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return vetsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<VetsDTO> result = vetsService.partialUpdate(vetsDTO);

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
     * {@code GET  /vets} : get all the vets.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vets in body.
     */
    @GetMapping("/vets")
    public Mono<ResponseEntity<List<VetsDTO>>> getAllVets(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Vets");
        return vetsService
            .countAll()
            .zipWith(vetsService.findAll(pageable).collectList())
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
     * {@code GET  /vets/:id} : get the "id" vets.
     *
     * @param id the id of the vetsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vetsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vets/{id}")
    public Mono<ResponseEntity<VetsDTO>> getVets(@PathVariable Long id) {
        log.debug("REST request to get Vets : {}", id);
        Mono<VetsDTO> vetsDTO = vetsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vetsDTO);
    }

    /**
     * {@code DELETE  /vets/:id} : delete the "id" vets.
     *
     * @param id the id of the vetsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/vets/{id}")
    public Mono<ResponseEntity<Void>> deleteVets(@PathVariable Long id) {
        log.debug("REST request to delete Vets : {}", id);
        return vetsService
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
     * {@code SEARCH  /_search/vets?query=:query} : search for the vets corresponding
     * to the query.
     *
     * @param query the query of the vets search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/vets")
    public Mono<ResponseEntity<Flux<VetsDTO>>> searchVets(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Vets for query {}", query);
        return vetsService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(vetsService.search(query, pageable)));
    }
}
