package br.com.rodrigoproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.rodrigoproject.IntegrationTest;
import br.com.rodrigoproject.domain.Tecnico;
import br.com.rodrigoproject.repository.TecnicoRepository;
import br.com.rodrigoproject.repository.search.TecnicoSearchRepository;
import br.com.rodrigoproject.service.dto.TecnicoDTO;
import br.com.rodrigoproject.service.mapper.TecnicoMapper;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TecnicoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TecnicoResourceIT {

    private static final String ENTITY_API_URL = "/api/tecnicos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/tecnicos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private TecnicoMapper tecnicoMapper;

    @Autowired
    private TecnicoSearchRepository tecnicoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTecnicoMockMvc;

    private Tecnico tecnico;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tecnico createEntity(EntityManager em) {
        Tecnico tecnico = new Tecnico();
        return tecnico;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tecnico createUpdatedEntity(EntityManager em) {
        Tecnico tecnico = new Tecnico();
        return tecnico;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        tecnicoSearchRepository.deleteAll();
        assertThat(tecnicoSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        tecnico = createEntity(em);
    }

    @Test
    @Transactional
    void createTecnico() throws Exception {
        int databaseSizeBeforeCreate = tecnicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        // Create the Tecnico
        TecnicoDTO tecnicoDTO = tecnicoMapper.toDto(tecnico);
        restTecnicoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tecnicoDTO)))
            .andExpect(status().isCreated());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Tecnico testTecnico = tecnicoList.get(tecnicoList.size() - 1);
    }

    @Test
    @Transactional
    void createTecnicoWithExistingId() throws Exception {
        // Create the Tecnico with an existing ID
        tecnico.setId(1L);
        TecnicoDTO tecnicoDTO = tecnicoMapper.toDto(tecnico);

        int databaseSizeBeforeCreate = tecnicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTecnicoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tecnicoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTecnicos() throws Exception {
        // Initialize the database
        tecnicoRepository.saveAndFlush(tecnico);

        // Get all the tecnicoList
        restTecnicoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tecnico.getId().intValue())));
    }

    @Test
    @Transactional
    void getTecnico() throws Exception {
        // Initialize the database
        tecnicoRepository.saveAndFlush(tecnico);

        // Get the tecnico
        restTecnicoMockMvc
            .perform(get(ENTITY_API_URL_ID, tecnico.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tecnico.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingTecnico() throws Exception {
        // Get the tecnico
        restTecnicoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTecnico() throws Exception {
        // Initialize the database
        tecnicoRepository.saveAndFlush(tecnico);

        int databaseSizeBeforeUpdate = tecnicoRepository.findAll().size();
        tecnicoSearchRepository.save(tecnico);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());

        // Update the tecnico
        Tecnico updatedTecnico = tecnicoRepository.findById(tecnico.getId()).get();
        // Disconnect from session so that the updates on updatedTecnico are not directly saved in db
        em.detach(updatedTecnico);
        TecnicoDTO tecnicoDTO = tecnicoMapper.toDto(updatedTecnico);

        restTecnicoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tecnicoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tecnicoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeUpdate);
        Tecnico testTecnico = tecnicoList.get(tecnicoList.size() - 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Tecnico> tecnicoSearchList = IterableUtils.toList(tecnicoSearchRepository.findAll());
                Tecnico testTecnicoSearch = tecnicoSearchList.get(searchDatabaseSizeAfter - 1);
            });
    }

    @Test
    @Transactional
    void putNonExistingTecnico() throws Exception {
        int databaseSizeBeforeUpdate = tecnicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        tecnico.setId(count.incrementAndGet());

        // Create the Tecnico
        TecnicoDTO tecnicoDTO = tecnicoMapper.toDto(tecnico);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTecnicoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tecnicoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tecnicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTecnico() throws Exception {
        int databaseSizeBeforeUpdate = tecnicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        tecnico.setId(count.incrementAndGet());

        // Create the Tecnico
        TecnicoDTO tecnicoDTO = tecnicoMapper.toDto(tecnico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTecnicoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tecnicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTecnico() throws Exception {
        int databaseSizeBeforeUpdate = tecnicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        tecnico.setId(count.incrementAndGet());

        // Create the Tecnico
        TecnicoDTO tecnicoDTO = tecnicoMapper.toDto(tecnico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTecnicoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tecnicoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTecnicoWithPatch() throws Exception {
        // Initialize the database
        tecnicoRepository.saveAndFlush(tecnico);

        int databaseSizeBeforeUpdate = tecnicoRepository.findAll().size();

        // Update the tecnico using partial update
        Tecnico partialUpdatedTecnico = new Tecnico();
        partialUpdatedTecnico.setId(tecnico.getId());

        restTecnicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTecnico.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTecnico))
            )
            .andExpect(status().isOk());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeUpdate);
        Tecnico testTecnico = tecnicoList.get(tecnicoList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateTecnicoWithPatch() throws Exception {
        // Initialize the database
        tecnicoRepository.saveAndFlush(tecnico);

        int databaseSizeBeforeUpdate = tecnicoRepository.findAll().size();

        // Update the tecnico using partial update
        Tecnico partialUpdatedTecnico = new Tecnico();
        partialUpdatedTecnico.setId(tecnico.getId());

        restTecnicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTecnico.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTecnico))
            )
            .andExpect(status().isOk());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeUpdate);
        Tecnico testTecnico = tecnicoList.get(tecnicoList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingTecnico() throws Exception {
        int databaseSizeBeforeUpdate = tecnicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        tecnico.setId(count.incrementAndGet());

        // Create the Tecnico
        TecnicoDTO tecnicoDTO = tecnicoMapper.toDto(tecnico);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTecnicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tecnicoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tecnicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTecnico() throws Exception {
        int databaseSizeBeforeUpdate = tecnicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        tecnico.setId(count.incrementAndGet());

        // Create the Tecnico
        TecnicoDTO tecnicoDTO = tecnicoMapper.toDto(tecnico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTecnicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tecnicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTecnico() throws Exception {
        int databaseSizeBeforeUpdate = tecnicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        tecnico.setId(count.incrementAndGet());

        // Create the Tecnico
        TecnicoDTO tecnicoDTO = tecnicoMapper.toDto(tecnico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTecnicoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tecnicoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tecnico in the database
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTecnico() throws Exception {
        // Initialize the database
        tecnicoRepository.saveAndFlush(tecnico);
        tecnicoRepository.save(tecnico);
        tecnicoSearchRepository.save(tecnico);

        int databaseSizeBeforeDelete = tecnicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the tecnico
        restTecnicoMockMvc
            .perform(delete(ENTITY_API_URL_ID, tecnico.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tecnico> tecnicoList = tecnicoRepository.findAll();
        assertThat(tecnicoList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tecnicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTecnico() throws Exception {
        // Initialize the database
        tecnico = tecnicoRepository.saveAndFlush(tecnico);
        tecnicoSearchRepository.save(tecnico);

        // Search the tecnico
        restTecnicoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + tecnico.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tecnico.getId().intValue())));
    }
}
