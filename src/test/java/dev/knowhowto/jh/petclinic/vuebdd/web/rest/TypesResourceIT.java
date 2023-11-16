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
import dev.knowhowto.jh.petclinic.vuebdd.domain.Types;
import dev.knowhowto.jh.petclinic.vuebdd.repository.EntityManager;
import dev.knowhowto.jh.petclinic.vuebdd.repository.TypesRepository;
import dev.knowhowto.jh.petclinic.vuebdd.repository.search.TypesSearchRepository;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.TypesDTO;
import dev.knowhowto.jh.petclinic.vuebdd.service.mapper.TypesMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link TypesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TypesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/types";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypesRepository typesRepository;

    @Autowired
    private TypesMapper typesMapper;

    @Autowired
    private TypesSearchRepository typesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Types types;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Types createEntity(EntityManager em) {
        Types types = new Types().name(DEFAULT_NAME);
        return types;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Types createUpdatedEntity(EntityManager em) {
        Types types = new Types().name(UPDATED_NAME);
        return types;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Types.class).block();
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
        typesSearchRepository.deleteAll().block();
        assertThat(typesSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        types = createEntity(em);
    }

    @Test
    void createTypes() throws Exception {
        int databaseSizeBeforeCreate = typesRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        // Create the Types
        TypesDTO typesDTO = typesMapper.toDto(types);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typesDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Types testTypes = typesList.get(typesList.size() - 1);
        assertThat(testTypes.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createTypesWithExistingId() throws Exception {
        // Create the Types with an existing ID
        types.setId(1L);
        TypesDTO typesDTO = typesMapper.toDto(types);

        int databaseSizeBeforeCreate = typesRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = typesRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        // set the field null
        types.setName(null);

        // Create the Types, which fails.
        TypesDTO typesDTO = typesMapper.toDto(types);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTypes() {
        // Initialize the database
        typesRepository.save(types).block();

        // Get all the typesList
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
            .value(hasItem(types.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @Test
    void getTypes() {
        // Initialize the database
        typesRepository.save(types).block();

        // Get the types
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, types.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(types.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingTypes() {
        // Get the types
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTypes() throws Exception {
        // Initialize the database
        typesRepository.save(types).block();

        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        typesSearchRepository.save(types).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());

        // Update the types
        Types updatedTypes = typesRepository.findById(types.getId()).block();
        updatedTypes.name(UPDATED_NAME);
        TypesDTO typesDTO = typesMapper.toDto(updatedTypes);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, typesDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typesDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        Types testTypes = typesList.get(typesList.size() - 1);
        assertThat(testTypes.getName()).isEqualTo(UPDATED_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Types> typesSearchList = IterableUtils.toList(typesSearchRepository.findAll().collectList().block());
                Types testTypesSearch = typesSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTypesSearch.getName()).isEqualTo(UPDATED_NAME);
            });
    }

    @Test
    void putNonExistingTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        types.setId(count.incrementAndGet());

        // Create the Types
        TypesDTO typesDTO = typesMapper.toDto(types);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, typesDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        types.setId(count.incrementAndGet());

        // Create the Types
        TypesDTO typesDTO = typesMapper.toDto(types);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        types.setId(count.incrementAndGet());

        // Create the Types
        TypesDTO typesDTO = typesMapper.toDto(types);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typesDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTypesWithPatch() throws Exception {
        // Initialize the database
        typesRepository.save(types).block();

        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();

        // Update the types using partial update
        Types partialUpdatedTypes = new Types();
        partialUpdatedTypes.setId(types.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTypes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        Types testTypes = typesList.get(typesList.size() - 1);
        assertThat(testTypes.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void fullUpdateTypesWithPatch() throws Exception {
        // Initialize the database
        typesRepository.save(types).block();

        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();

        // Update the types using partial update
        Types partialUpdatedTypes = new Types();
        partialUpdatedTypes.setId(types.getId());

        partialUpdatedTypes.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTypes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        Types testTypes = typesList.get(typesList.size() - 1);
        assertThat(testTypes.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        types.setId(count.incrementAndGet());

        // Create the Types
        TypesDTO typesDTO = typesMapper.toDto(types);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, typesDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        types.setId(count.incrementAndGet());

        // Create the Types
        TypesDTO typesDTO = typesMapper.toDto(types);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTypes() throws Exception {
        int databaseSizeBeforeUpdate = typesRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        types.setId(count.incrementAndGet());

        // Create the Types
        TypesDTO typesDTO = typesMapper.toDto(types);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typesDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Types in the database
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTypes() {
        // Initialize the database
        typesRepository.save(types).block();
        typesRepository.save(types).block();
        typesSearchRepository.save(types).block();

        int databaseSizeBeforeDelete = typesRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the types
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, types.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Types> typesList = typesRepository.findAll().collectList().block();
        assertThat(typesList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(typesSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTypes() {
        // Initialize the database
        types = typesRepository.save(types).block();
        typesSearchRepository.save(types).block();

        // Search the types
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + types.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(types.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }
}
