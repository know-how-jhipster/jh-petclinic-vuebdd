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
import dev.knowhowto.jh.petclinic.vuebdd.domain.Owners;
import dev.knowhowto.jh.petclinic.vuebdd.repository.EntityManager;
import dev.knowhowto.jh.petclinic.vuebdd.repository.OwnersRepository;
import dev.knowhowto.jh.petclinic.vuebdd.repository.search.OwnersSearchRepository;
import dev.knowhowto.jh.petclinic.vuebdd.service.dto.OwnersDTO;
import dev.knowhowto.jh.petclinic.vuebdd.service.mapper.OwnersMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link OwnersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OwnersResourceIT {

    private static final String DEFAULT_FIRSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LASTNAME = "AAAAAAAAAA";
    private static final String UPDATED_LASTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/owners";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/owners";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OwnersRepository ownersRepository;

    @Autowired
    private OwnersMapper ownersMapper;

    @Autowired
    private OwnersSearchRepository ownersSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Owners owners;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Owners createEntity(EntityManager em) {
        Owners owners = new Owners()
            .firstname(DEFAULT_FIRSTNAME)
            .lastname(DEFAULT_LASTNAME)
            .address(DEFAULT_ADDRESS)
            .city(DEFAULT_CITY)
            .telephone(DEFAULT_TELEPHONE);
        return owners;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Owners createUpdatedEntity(EntityManager em) {
        Owners owners = new Owners()
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .telephone(UPDATED_TELEPHONE);
        return owners;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Owners.class).block();
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
        ownersSearchRepository.deleteAll().block();
        assertThat(ownersSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        owners = createEntity(em);
    }

    @Test
    void createOwners() throws Exception {
        int databaseSizeBeforeCreate = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        // Create the Owners
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Owners testOwners = ownersList.get(ownersList.size() - 1);
        assertThat(testOwners.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testOwners.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testOwners.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testOwners.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testOwners.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
    }

    @Test
    void createOwnersWithExistingId() throws Exception {
        // Create the Owners with an existing ID
        owners.setId(1L);
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        int databaseSizeBeforeCreate = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkFirstnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        // set the field null
        owners.setFirstname(null);

        // Create the Owners, which fails.
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkLastnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        // set the field null
        owners.setLastname(null);

        // Create the Owners, which fails.
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        // set the field null
        owners.setAddress(null);

        // Create the Owners, which fails.
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTelephoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        // set the field null
        owners.setTelephone(null);

        // Create the Owners, which fails.
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllOwners() {
        // Initialize the database
        ownersRepository.save(owners).block();

        // Get all the ownersList
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
            .value(hasItem(owners.getId().intValue()))
            .jsonPath("$.[*].firstname")
            .value(hasItem(DEFAULT_FIRSTNAME))
            .jsonPath("$.[*].lastname")
            .value(hasItem(DEFAULT_LASTNAME))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].telephone")
            .value(hasItem(DEFAULT_TELEPHONE));
    }

    @Test
    void getOwners() {
        // Initialize the database
        ownersRepository.save(owners).block();

        // Get the owners
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, owners.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(owners.getId().intValue()))
            .jsonPath("$.firstname")
            .value(is(DEFAULT_FIRSTNAME))
            .jsonPath("$.lastname")
            .value(is(DEFAULT_LASTNAME))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.telephone")
            .value(is(DEFAULT_TELEPHONE));
    }

    @Test
    void getNonExistingOwners() {
        // Get the owners
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOwners() throws Exception {
        // Initialize the database
        ownersRepository.save(owners).block();

        int databaseSizeBeforeUpdate = ownersRepository.findAll().collectList().block().size();
        ownersSearchRepository.save(owners).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());

        // Update the owners
        Owners updatedOwners = ownersRepository.findById(owners.getId()).block();
        updatedOwners
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .telephone(UPDATED_TELEPHONE);
        OwnersDTO ownersDTO = ownersMapper.toDto(updatedOwners);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ownersDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeUpdate);
        Owners testOwners = ownersList.get(ownersList.size() - 1);
        assertThat(testOwners.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testOwners.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testOwners.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testOwners.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testOwners.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Owners> ownersSearchList = IterableUtils.toList(ownersSearchRepository.findAll().collectList().block());
                Owners testOwnersSearch = ownersSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOwnersSearch.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
                assertThat(testOwnersSearch.getLastname()).isEqualTo(UPDATED_LASTNAME);
                assertThat(testOwnersSearch.getAddress()).isEqualTo(UPDATED_ADDRESS);
                assertThat(testOwnersSearch.getCity()).isEqualTo(UPDATED_CITY);
                assertThat(testOwnersSearch.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
            });
    }

    @Test
    void putNonExistingOwners() throws Exception {
        int databaseSizeBeforeUpdate = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        owners.setId(count.incrementAndGet());

        // Create the Owners
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ownersDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchOwners() throws Exception {
        int databaseSizeBeforeUpdate = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        owners.setId(count.incrementAndGet());

        // Create the Owners
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamOwners() throws Exception {
        int databaseSizeBeforeUpdate = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        owners.setId(count.incrementAndGet());

        // Create the Owners
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateOwnersWithPatch() throws Exception {
        // Initialize the database
        ownersRepository.save(owners).block();

        int databaseSizeBeforeUpdate = ownersRepository.findAll().collectList().block().size();

        // Update the owners using partial update
        Owners partialUpdatedOwners = new Owners();
        partialUpdatedOwners.setId(owners.getId());

        partialUpdatedOwners.address(UPDATED_ADDRESS).telephone(UPDATED_TELEPHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOwners.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOwners))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeUpdate);
        Owners testOwners = ownersList.get(ownersList.size() - 1);
        assertThat(testOwners.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testOwners.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testOwners.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testOwners.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testOwners.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
    }

    @Test
    void fullUpdateOwnersWithPatch() throws Exception {
        // Initialize the database
        ownersRepository.save(owners).block();

        int databaseSizeBeforeUpdate = ownersRepository.findAll().collectList().block().size();

        // Update the owners using partial update
        Owners partialUpdatedOwners = new Owners();
        partialUpdatedOwners.setId(owners.getId());

        partialUpdatedOwners
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .telephone(UPDATED_TELEPHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOwners.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOwners))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeUpdate);
        Owners testOwners = ownersList.get(ownersList.size() - 1);
        assertThat(testOwners.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testOwners.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testOwners.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testOwners.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testOwners.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
    }

    @Test
    void patchNonExistingOwners() throws Exception {
        int databaseSizeBeforeUpdate = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        owners.setId(count.incrementAndGet());

        // Create the Owners
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ownersDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchOwners() throws Exception {
        int databaseSizeBeforeUpdate = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        owners.setId(count.incrementAndGet());

        // Create the Owners
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamOwners() throws Exception {
        int databaseSizeBeforeUpdate = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        owners.setId(count.incrementAndGet());

        // Create the Owners
        OwnersDTO ownersDTO = ownersMapper.toDto(owners);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ownersDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Owners in the database
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteOwners() {
        // Initialize the database
        ownersRepository.save(owners).block();
        ownersRepository.save(owners).block();
        ownersSearchRepository.save(owners).block();

        int databaseSizeBeforeDelete = ownersRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the owners
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, owners.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Owners> ownersList = ownersRepository.findAll().collectList().block();
        assertThat(ownersList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ownersSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchOwners() {
        // Initialize the database
        owners = ownersRepository.save(owners).block();
        ownersSearchRepository.save(owners).block();

        // Search the owners
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + owners.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(owners.getId().intValue()))
            .jsonPath("$.[*].firstname")
            .value(hasItem(DEFAULT_FIRSTNAME))
            .jsonPath("$.[*].lastname")
            .value(hasItem(DEFAULT_LASTNAME))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].telephone")
            .value(hasItem(DEFAULT_TELEPHONE));
    }
}
