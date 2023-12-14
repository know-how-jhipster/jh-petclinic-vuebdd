package dev.knowhowto.jh.petclinic.vuebdd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import java.time.Duration;
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
import dev.knowhowto.jh.petclinic.vuebdd.domain.Vets;
import dev.knowhowto.jh.petclinic.vuebdd.repository.EntityManager;
import dev.knowhowto.jh.petclinic.vuebdd.repository.VetsRepository;
import dev.knowhowto.jh.petclinic.vuebdd.repository.search.VetsSearchRepository;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.VetsDTO;
import dev.knowhowto.jh.petclinic.vuebdd.service.mapper.VetsMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link VetsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class VetsResourceIT {

    private static final String DEFAULT_FIRSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LASTNAME = "AAAAAAAAAA";
    private static final String UPDATED_LASTNAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/vets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/vets";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VetsRepository vetsRepository;

    @Autowired
    private VetsMapper vetsMapper;

    @Autowired
    private VetsSearchRepository vetsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Vets vets;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vets createEntity(EntityManager em) {
        Vets vets = new Vets().firstname(DEFAULT_FIRSTNAME).lastname(DEFAULT_LASTNAME);
        return vets;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vets createUpdatedEntity(EntityManager em) {
        Vets vets = new Vets().firstname(UPDATED_FIRSTNAME).lastname(UPDATED_LASTNAME);
        return vets;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Vets.class).block();
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
        vetsSearchRepository.deleteAll().block();
        assertThat(vetsSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        vets = createEntity(em);
    }

    @Test
    void createVets() throws Exception {
        int databaseSizeBeforeCreate = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        // Create the Vets
        VetsDTO vetsDTO = vetsMapper.toDto(vets);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Vets testVets = vetsList.get(vetsList.size() - 1);
        assertThat(testVets.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testVets.getLastname()).isEqualTo(DEFAULT_LASTNAME);
    }

    @Test
    void createVetsWithExistingId() throws Exception {
        // Create the Vets with an existing ID
        vets.setId(1L);
        VetsDTO vetsDTO = vetsMapper.toDto(vets);

        int databaseSizeBeforeCreate = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkFirstnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        // set the field null
        vets.setFirstname(null);

        // Create the Vets, which fails.
        VetsDTO vetsDTO = vetsMapper.toDto(vets);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkLastnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        // set the field null
        vets.setLastname(null);

        // Create the Vets, which fails.
        VetsDTO vetsDTO = vetsMapper.toDto(vets);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllVets() {
        // Initialize the database
        vetsRepository.save(vets).block();

        // Get all the vetsList
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
            .value(hasItem(vets.getId().intValue()))
            .jsonPath("$.[*].firstname")
            .value(hasItem(DEFAULT_FIRSTNAME))
            .jsonPath("$.[*].lastname")
            .value(hasItem(DEFAULT_LASTNAME));
    }

    @Test
    void getVets() {
        // Initialize the database
        vetsRepository.save(vets).block();

        // Get the vets
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, vets.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(vets.getId().intValue()))
            .jsonPath("$.firstname")
            .value(is(DEFAULT_FIRSTNAME))
            .jsonPath("$.lastname")
            .value(is(DEFAULT_LASTNAME));
    }

    @Test
    void getNonExistingVets() {
        // Get the vets
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingVets() throws Exception {
        // Initialize the database
        vetsRepository.save(vets).block();

        int databaseSizeBeforeUpdate = vetsRepository.findAll().collectList().block().size();
        vetsSearchRepository.save(vets).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());

        // Update the vets
        Vets updatedVets = vetsRepository.findById(vets.getId()).block();
        updatedVets.firstname(UPDATED_FIRSTNAME).lastname(UPDATED_LASTNAME);
        VetsDTO vetsDTO = vetsMapper.toDto(updatedVets);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, vetsDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeUpdate);
        Vets testVets = vetsList.get(vetsList.size() - 1);
        assertThat(testVets.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testVets.getLastname()).isEqualTo(UPDATED_LASTNAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Vets> vetsSearchList = IterableUtils.toList(vetsSearchRepository.findAll().collectList().block());
                Vets testVetsSearch = vetsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testVetsSearch.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
                assertThat(testVetsSearch.getLastname()).isEqualTo(UPDATED_LASTNAME);
            });
    }

    @Test
    void putNonExistingVets() throws Exception {
        int databaseSizeBeforeUpdate = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        vets.setId(count.incrementAndGet());

        // Create the Vets
        VetsDTO vetsDTO = vetsMapper.toDto(vets);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, vetsDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchVets() throws Exception {
        int databaseSizeBeforeUpdate = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        vets.setId(count.incrementAndGet());

        // Create the Vets
        VetsDTO vetsDTO = vetsMapper.toDto(vets);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamVets() throws Exception {
        int databaseSizeBeforeUpdate = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        vets.setId(count.incrementAndGet());

        // Create the Vets
        VetsDTO vetsDTO = vetsMapper.toDto(vets);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateVetsWithPatch() throws Exception {
        // Initialize the database
        vetsRepository.save(vets).block();

        int databaseSizeBeforeUpdate = vetsRepository.findAll().collectList().block().size();

        // Update the vets using partial update
        Vets partialUpdatedVets = new Vets();
        partialUpdatedVets.setId(vets.getId());

        partialUpdatedVets.lastname(UPDATED_LASTNAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVets.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVets))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeUpdate);
        Vets testVets = vetsList.get(vetsList.size() - 1);
        assertThat(testVets.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testVets.getLastname()).isEqualTo(UPDATED_LASTNAME);
    }

    @Test
    void fullUpdateVetsWithPatch() throws Exception {
        // Initialize the database
        vetsRepository.save(vets).block();

        int databaseSizeBeforeUpdate = vetsRepository.findAll().collectList().block().size();

        // Update the vets using partial update
        Vets partialUpdatedVets = new Vets();
        partialUpdatedVets.setId(vets.getId());

        partialUpdatedVets.firstname(UPDATED_FIRSTNAME).lastname(UPDATED_LASTNAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVets.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVets))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeUpdate);
        Vets testVets = vetsList.get(vetsList.size() - 1);
        assertThat(testVets.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testVets.getLastname()).isEqualTo(UPDATED_LASTNAME);
    }

    @Test
    void patchNonExistingVets() throws Exception {
        int databaseSizeBeforeUpdate = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        vets.setId(count.incrementAndGet());

        // Create the Vets
        VetsDTO vetsDTO = vetsMapper.toDto(vets);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, vetsDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchVets() throws Exception {
        int databaseSizeBeforeUpdate = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        vets.setId(count.incrementAndGet());

        // Create the Vets
        VetsDTO vetsDTO = vetsMapper.toDto(vets);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamVets() throws Exception {
        int databaseSizeBeforeUpdate = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        vets.setId(count.incrementAndGet());

        // Create the Vets
        VetsDTO vetsDTO = vetsMapper.toDto(vets);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(vetsDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Vets in the database
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteVets() {
        // Initialize the database
        vetsRepository.save(vets).block();
        vetsRepository.save(vets).block();
        vetsSearchRepository.save(vets).block();

        int databaseSizeBeforeDelete = vetsRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the vets
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, vets.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Vets> vetsList = vetsRepository.findAll().collectList().block();
        assertThat(vetsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(vetsSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchVets() {
        // Initialize the database
        vets = vetsRepository.save(vets).block();
        vetsSearchRepository.save(vets).block();

        // Search the vets
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + vets.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(vets.getId().intValue()))
            .jsonPath("$.[*].firstname")
            .value(hasItem(DEFAULT_FIRSTNAME))
            .jsonPath("$.[*].lastname")
            .value(hasItem(DEFAULT_LASTNAME));
    }
}
