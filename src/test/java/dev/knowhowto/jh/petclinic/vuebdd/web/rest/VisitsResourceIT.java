package dev.knowhowto.jh.petclinic.vuebdd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import dev.knowhowto.jh.petclinic.vuebdd.IntegrationTest;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Visits;
import dev.knowhowto.jh.petclinic.vuebdd.repository.EntityManager;
import dev.knowhowto.jh.petclinic.vuebdd.repository.VisitsRepository;
import dev.knowhowto.jh.petclinic.vuebdd.repository.search.VisitsSearchRepository;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.VisitsDTO;
import dev.knowhowto.jh.petclinic.vuebdd.service.mapper.VisitsMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link VisitsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class VisitsResourceIT {

    private static final Instant DEFAULT_VISITDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VISITDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/visits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/visits";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VisitsRepository visitsRepository;

    @Autowired
    private VisitsMapper visitsMapper;

    @Autowired
    private VisitsSearchRepository visitsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Visits visits;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visits createEntity(EntityManager em) {
        Visits visits = new Visits().visitdate(DEFAULT_VISITDATE).description(DEFAULT_DESCRIPTION);
        return visits;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visits createUpdatedEntity(EntityManager em) {
        Visits visits = new Visits().visitdate(UPDATED_VISITDATE).description(UPDATED_DESCRIPTION);
        return visits;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Visits.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        visitsSearchRepository.deleteAll().block();
        assertThat(visitsSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        visits = createEntity(em);
    }

    @Test
    void createVisits() throws Exception {
        int databaseSizeBeforeCreate = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        // Create the Visits
        VisitsDTO visitsDTO = visitsMapper.toDto(visits);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Visits testVisits = visitsList.get(visitsList.size() - 1);
        assertThat(testVisits.getVisitdate()).isEqualTo(DEFAULT_VISITDATE);
        assertThat(testVisits.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createVisitsWithExistingId() throws Exception {
        // Create the Visits with an existing ID
        visits.setId(1L);
        VisitsDTO visitsDTO = visitsMapper.toDto(visits);

        int databaseSizeBeforeCreate = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkVisitdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        // set the field null
        visits.setVisitdate(null);

        // Create the Visits, which fails.
        VisitsDTO visitsDTO = visitsMapper.toDto(visits);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        // set the field null
        visits.setDescription(null);

        // Create the Visits, which fails.
        VisitsDTO visitsDTO = visitsMapper.toDto(visits);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllVisits() {
        // Initialize the database
        visitsRepository.save(visits).block();

        // Get all the visitsList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(visits.getId().intValue()))
            .jsonPath("$.[*].visitdate")
            .value(hasItem(DEFAULT_VISITDATE.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getVisits() {
        // Initialize the database
        visitsRepository.save(visits).block();

        // Get the visits
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, visits.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(visits.getId().intValue()))
            .jsonPath("$.visitdate")
            .value(is(DEFAULT_VISITDATE.toString()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingVisits() {
        // Get the visits
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingVisits() throws Exception {
        // Initialize the database
        visitsRepository.save(visits).block();

        int databaseSizeBeforeUpdate = visitsRepository.findAll().collectList().block().size();
        visitsSearchRepository.save(visits).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());

        // Update the visits
        Visits updatedVisits = visitsRepository.findById(visits.getId()).block();
        updatedVisits.visitdate(UPDATED_VISITDATE).description(UPDATED_DESCRIPTION);
        VisitsDTO visitsDTO = visitsMapper.toDto(updatedVisits);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, visitsDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeUpdate);
        Visits testVisits = visitsList.get(visitsList.size() - 1);
        assertThat(testVisits.getVisitdate()).isEqualTo(UPDATED_VISITDATE);
        assertThat(testVisits.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Visits> visitsSearchList = IterableUtils.toList(visitsSearchRepository.findAll().collectList().block());
                Visits testVisitsSearch = visitsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testVisitsSearch.getVisitdate()).isEqualTo(UPDATED_VISITDATE);
                assertThat(testVisitsSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    void putNonExistingVisits() throws Exception {
        int databaseSizeBeforeUpdate = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        visits.setId(count.incrementAndGet());

        // Create the Visits
        VisitsDTO visitsDTO = visitsMapper.toDto(visits);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, visitsDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchVisits() throws Exception {
        int databaseSizeBeforeUpdate = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        visits.setId(count.incrementAndGet());

        // Create the Visits
        VisitsDTO visitsDTO = visitsMapper.toDto(visits);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamVisits() throws Exception {
        int databaseSizeBeforeUpdate = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        visits.setId(count.incrementAndGet());

        // Create the Visits
        VisitsDTO visitsDTO = visitsMapper.toDto(visits);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateVisitsWithPatch() throws Exception {
        // Initialize the database
        visitsRepository.save(visits).block();

        int databaseSizeBeforeUpdate = visitsRepository.findAll().collectList().block().size();

        // Update the visits using partial update
        Visits partialUpdatedVisits = new Visits();
        partialUpdatedVisits.setId(visits.getId());

        partialUpdatedVisits.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVisits.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVisits))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeUpdate);
        Visits testVisits = visitsList.get(visitsList.size() - 1);
        assertThat(testVisits.getVisitdate()).isEqualTo(DEFAULT_VISITDATE);
        assertThat(testVisits.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateVisitsWithPatch() throws Exception {
        // Initialize the database
        visitsRepository.save(visits).block();

        int databaseSizeBeforeUpdate = visitsRepository.findAll().collectList().block().size();

        // Update the visits using partial update
        Visits partialUpdatedVisits = new Visits();
        partialUpdatedVisits.setId(visits.getId());

        partialUpdatedVisits.visitdate(UPDATED_VISITDATE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVisits.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVisits))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeUpdate);
        Visits testVisits = visitsList.get(visitsList.size() - 1);
        assertThat(testVisits.getVisitdate()).isEqualTo(UPDATED_VISITDATE);
        assertThat(testVisits.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingVisits() throws Exception {
        int databaseSizeBeforeUpdate = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        visits.setId(count.incrementAndGet());

        // Create the Visits
        VisitsDTO visitsDTO = visitsMapper.toDto(visits);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, visitsDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchVisits() throws Exception {
        int databaseSizeBeforeUpdate = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        visits.setId(count.incrementAndGet());

        // Create the Visits
        VisitsDTO visitsDTO = visitsMapper.toDto(visits);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamVisits() throws Exception {
        int databaseSizeBeforeUpdate = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        visits.setId(count.incrementAndGet());

        // Create the Visits
        VisitsDTO visitsDTO = visitsMapper.toDto(visits);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(visitsDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Visits in the database
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteVisits() {
        // Initialize the database
        visitsRepository.save(visits).block();
        visitsRepository.save(visits).block();
        visitsSearchRepository.save(visits).block();

        int databaseSizeBeforeDelete = visitsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the visits
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, visits.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Visits> visitsList = visitsRepository.findAll().collectList().block();
        assertThat(visitsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(visitsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchVisits() {
        // Initialize the database
        visits = visitsRepository.save(visits).block();
        visitsSearchRepository.save(visits).block();

        // Search the visits
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + visits.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(visits.getId().intValue()))
            .jsonPath("$.[*].visitdate")
            .value(hasItem(DEFAULT_VISITDATE.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }
}
