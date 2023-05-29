package br.com.rodrigoproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.rodrigoproject.IntegrationTest;
import br.com.rodrigoproject.domain.OrderServico;
import br.com.rodrigoproject.domain.enumeration.Prioridade;
import br.com.rodrigoproject.domain.enumeration.Status;
import br.com.rodrigoproject.repository.OrderServicoRepository;
import br.com.rodrigoproject.repository.search.OrderServicoSearchRepository;
import br.com.rodrigoproject.service.dto.OrderServicoDTO;
import br.com.rodrigoproject.service.mapper.OrderServicoMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link OrderServicoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderServicoResourceIT {

    private static final LocalDate DEFAULT_DATA_ABERTURA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_ABERTURA = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATA_FECHAMENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_FECHAMENTO = LocalDate.now(ZoneId.systemDefault());

    private static final Prioridade DEFAULT_PRIORIDADE = Prioridade.BAIXA;
    private static final Prioridade UPDATED_PRIORIDADE = Prioridade.MEDIA;

    private static final String DEFAULT_OBSERVACOES = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVACOES = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ABERTO;
    private static final Status UPDATED_STATUS = Status.EMANDAMENTO;

    private static final String ENTITY_API_URL = "/api/order-servicos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/order-servicos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderServicoRepository orderServicoRepository;

    @Autowired
    private OrderServicoMapper orderServicoMapper;

    @Autowired
    private OrderServicoSearchRepository orderServicoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderServicoMockMvc;

    private OrderServico orderServico;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderServico createEntity(EntityManager em) {
        OrderServico orderServico = new OrderServico()
            .dataAbertura(DEFAULT_DATA_ABERTURA)
            .dataFechamento(DEFAULT_DATA_FECHAMENTO)
            .prioridade(DEFAULT_PRIORIDADE)
            .observacoes(DEFAULT_OBSERVACOES)
            .status(DEFAULT_STATUS);
        return orderServico;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderServico createUpdatedEntity(EntityManager em) {
        OrderServico orderServico = new OrderServico()
            .dataAbertura(UPDATED_DATA_ABERTURA)
            .dataFechamento(UPDATED_DATA_FECHAMENTO)
            .prioridade(UPDATED_PRIORIDADE)
            .observacoes(UPDATED_OBSERVACOES)
            .status(UPDATED_STATUS);
        return orderServico;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        orderServicoSearchRepository.deleteAll();
        assertThat(orderServicoSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        orderServico = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderServico() throws Exception {
        int databaseSizeBeforeCreate = orderServicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        // Create the OrderServico
        OrderServicoDTO orderServicoDTO = orderServicoMapper.toDto(orderServico);
        restOrderServicoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderServicoDTO))
            )
            .andExpect(status().isCreated());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        OrderServico testOrderServico = orderServicoList.get(orderServicoList.size() - 1);
        assertThat(testOrderServico.getDataAbertura()).isEqualTo(DEFAULT_DATA_ABERTURA);
        assertThat(testOrderServico.getDataFechamento()).isEqualTo(DEFAULT_DATA_FECHAMENTO);
        assertThat(testOrderServico.getPrioridade()).isEqualTo(DEFAULT_PRIORIDADE);
        assertThat(testOrderServico.getObservacoes()).isEqualTo(DEFAULT_OBSERVACOES);
        assertThat(testOrderServico.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createOrderServicoWithExistingId() throws Exception {
        // Create the OrderServico with an existing ID
        orderServico.setId(1L);
        OrderServicoDTO orderServicoDTO = orderServicoMapper.toDto(orderServico);

        int databaseSizeBeforeCreate = orderServicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderServicoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderServicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOrderServicos() throws Exception {
        // Initialize the database
        orderServicoRepository.saveAndFlush(orderServico);

        // Get all the orderServicoList
        restOrderServicoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderServico.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataAbertura").value(hasItem(DEFAULT_DATA_ABERTURA.toString())))
            .andExpect(jsonPath("$.[*].dataFechamento").value(hasItem(DEFAULT_DATA_FECHAMENTO.toString())))
            .andExpect(jsonPath("$.[*].prioridade").value(hasItem(DEFAULT_PRIORIDADE.toString())))
            .andExpect(jsonPath("$.[*].observacoes").value(hasItem(DEFAULT_OBSERVACOES)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getOrderServico() throws Exception {
        // Initialize the database
        orderServicoRepository.saveAndFlush(orderServico);

        // Get the orderServico
        restOrderServicoMockMvc
            .perform(get(ENTITY_API_URL_ID, orderServico.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderServico.getId().intValue()))
            .andExpect(jsonPath("$.dataAbertura").value(DEFAULT_DATA_ABERTURA.toString()))
            .andExpect(jsonPath("$.dataFechamento").value(DEFAULT_DATA_FECHAMENTO.toString()))
            .andExpect(jsonPath("$.prioridade").value(DEFAULT_PRIORIDADE.toString()))
            .andExpect(jsonPath("$.observacoes").value(DEFAULT_OBSERVACOES))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOrderServico() throws Exception {
        // Get the orderServico
        restOrderServicoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderServico() throws Exception {
        // Initialize the database
        orderServicoRepository.saveAndFlush(orderServico);

        int databaseSizeBeforeUpdate = orderServicoRepository.findAll().size();
        orderServicoSearchRepository.save(orderServico);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());

        // Update the orderServico
        OrderServico updatedOrderServico = orderServicoRepository.findById(orderServico.getId()).get();
        // Disconnect from session so that the updates on updatedOrderServico are not directly saved in db
        em.detach(updatedOrderServico);
        updatedOrderServico
            .dataAbertura(UPDATED_DATA_ABERTURA)
            .dataFechamento(UPDATED_DATA_FECHAMENTO)
            .prioridade(UPDATED_PRIORIDADE)
            .observacoes(UPDATED_OBSERVACOES)
            .status(UPDATED_STATUS);
        OrderServicoDTO orderServicoDTO = orderServicoMapper.toDto(updatedOrderServico);

        restOrderServicoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderServicoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderServicoDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeUpdate);
        OrderServico testOrderServico = orderServicoList.get(orderServicoList.size() - 1);
        assertThat(testOrderServico.getDataAbertura()).isEqualTo(UPDATED_DATA_ABERTURA);
        assertThat(testOrderServico.getDataFechamento()).isEqualTo(UPDATED_DATA_FECHAMENTO);
        assertThat(testOrderServico.getPrioridade()).isEqualTo(UPDATED_PRIORIDADE);
        assertThat(testOrderServico.getObservacoes()).isEqualTo(UPDATED_OBSERVACOES);
        assertThat(testOrderServico.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<OrderServico> orderServicoSearchList = IterableUtils.toList(orderServicoSearchRepository.findAll());
                OrderServico testOrderServicoSearch = orderServicoSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOrderServicoSearch.getDataAbertura()).isEqualTo(UPDATED_DATA_ABERTURA);
                assertThat(testOrderServicoSearch.getDataFechamento()).isEqualTo(UPDATED_DATA_FECHAMENTO);
                assertThat(testOrderServicoSearch.getPrioridade()).isEqualTo(UPDATED_PRIORIDADE);
                assertThat(testOrderServicoSearch.getObservacoes()).isEqualTo(UPDATED_OBSERVACOES);
                assertThat(testOrderServicoSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingOrderServico() throws Exception {
        int databaseSizeBeforeUpdate = orderServicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        orderServico.setId(count.incrementAndGet());

        // Create the OrderServico
        OrderServicoDTO orderServicoDTO = orderServicoMapper.toDto(orderServico);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderServicoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderServicoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderServicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderServico() throws Exception {
        int databaseSizeBeforeUpdate = orderServicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        orderServico.setId(count.incrementAndGet());

        // Create the OrderServico
        OrderServicoDTO orderServicoDTO = orderServicoMapper.toDto(orderServico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderServicoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderServicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderServico() throws Exception {
        int databaseSizeBeforeUpdate = orderServicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        orderServico.setId(count.incrementAndGet());

        // Create the OrderServico
        OrderServicoDTO orderServicoDTO = orderServicoMapper.toDto(orderServico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderServicoMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderServicoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOrderServicoWithPatch() throws Exception {
        // Initialize the database
        orderServicoRepository.saveAndFlush(orderServico);

        int databaseSizeBeforeUpdate = orderServicoRepository.findAll().size();

        // Update the orderServico using partial update
        OrderServico partialUpdatedOrderServico = new OrderServico();
        partialUpdatedOrderServico.setId(orderServico.getId());

        partialUpdatedOrderServico
            .dataAbertura(UPDATED_DATA_ABERTURA)
            .dataFechamento(UPDATED_DATA_FECHAMENTO)
            .prioridade(UPDATED_PRIORIDADE)
            .status(UPDATED_STATUS);

        restOrderServicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderServico.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderServico))
            )
            .andExpect(status().isOk());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeUpdate);
        OrderServico testOrderServico = orderServicoList.get(orderServicoList.size() - 1);
        assertThat(testOrderServico.getDataAbertura()).isEqualTo(UPDATED_DATA_ABERTURA);
        assertThat(testOrderServico.getDataFechamento()).isEqualTo(UPDATED_DATA_FECHAMENTO);
        assertThat(testOrderServico.getPrioridade()).isEqualTo(UPDATED_PRIORIDADE);
        assertThat(testOrderServico.getObservacoes()).isEqualTo(DEFAULT_OBSERVACOES);
        assertThat(testOrderServico.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateOrderServicoWithPatch() throws Exception {
        // Initialize the database
        orderServicoRepository.saveAndFlush(orderServico);

        int databaseSizeBeforeUpdate = orderServicoRepository.findAll().size();

        // Update the orderServico using partial update
        OrderServico partialUpdatedOrderServico = new OrderServico();
        partialUpdatedOrderServico.setId(orderServico.getId());

        partialUpdatedOrderServico
            .dataAbertura(UPDATED_DATA_ABERTURA)
            .dataFechamento(UPDATED_DATA_FECHAMENTO)
            .prioridade(UPDATED_PRIORIDADE)
            .observacoes(UPDATED_OBSERVACOES)
            .status(UPDATED_STATUS);

        restOrderServicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderServico.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderServico))
            )
            .andExpect(status().isOk());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeUpdate);
        OrderServico testOrderServico = orderServicoList.get(orderServicoList.size() - 1);
        assertThat(testOrderServico.getDataAbertura()).isEqualTo(UPDATED_DATA_ABERTURA);
        assertThat(testOrderServico.getDataFechamento()).isEqualTo(UPDATED_DATA_FECHAMENTO);
        assertThat(testOrderServico.getPrioridade()).isEqualTo(UPDATED_PRIORIDADE);
        assertThat(testOrderServico.getObservacoes()).isEqualTo(UPDATED_OBSERVACOES);
        assertThat(testOrderServico.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingOrderServico() throws Exception {
        int databaseSizeBeforeUpdate = orderServicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        orderServico.setId(count.incrementAndGet());

        // Create the OrderServico
        OrderServicoDTO orderServicoDTO = orderServicoMapper.toDto(orderServico);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderServicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderServicoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderServicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderServico() throws Exception {
        int databaseSizeBeforeUpdate = orderServicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        orderServico.setId(count.incrementAndGet());

        // Create the OrderServico
        OrderServicoDTO orderServicoDTO = orderServicoMapper.toDto(orderServico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderServicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderServicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderServico() throws Exception {
        int databaseSizeBeforeUpdate = orderServicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        orderServico.setId(count.incrementAndGet());

        // Create the OrderServico
        OrderServicoDTO orderServicoDTO = orderServicoMapper.toDto(orderServico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderServicoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderServicoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderServico in the database
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOrderServico() throws Exception {
        // Initialize the database
        orderServicoRepository.saveAndFlush(orderServico);
        orderServicoRepository.save(orderServico);
        orderServicoSearchRepository.save(orderServico);

        int databaseSizeBeforeDelete = orderServicoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the orderServico
        restOrderServicoMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderServico.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderServico> orderServicoList = orderServicoRepository.findAll();
        assertThat(orderServicoList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderServicoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOrderServico() throws Exception {
        // Initialize the database
        orderServico = orderServicoRepository.saveAndFlush(orderServico);
        orderServicoSearchRepository.save(orderServico);

        // Search the orderServico
        restOrderServicoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + orderServico.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderServico.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataAbertura").value(hasItem(DEFAULT_DATA_ABERTURA.toString())))
            .andExpect(jsonPath("$.[*].dataFechamento").value(hasItem(DEFAULT_DATA_FECHAMENTO.toString())))
            .andExpect(jsonPath("$.[*].prioridade").value(hasItem(DEFAULT_PRIORIDADE.toString())))
            .andExpect(jsonPath("$.[*].observacoes").value(hasItem(DEFAULT_OBSERVACOES)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
