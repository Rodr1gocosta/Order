package br.com.rodrigoproject.service;

import br.com.rodrigoproject.domain.OrderServico;
import br.com.rodrigoproject.domain.enumeration.Status;
import br.com.rodrigoproject.repository.OrderServicoRepository;
import br.com.rodrigoproject.repository.search.OrderServicoSearchRepository;
import br.com.rodrigoproject.service.dto.OrderServicoDTO;
import br.com.rodrigoproject.service.mapper.OrderServicoMapper;
import br.com.rodrigoproject.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service Implementation for managing {@link OrderServico}.
 */
@Service
@Transactional
public class OrderServicoService {

    private final Logger log = LoggerFactory.getLogger(OrderServicoService.class);

    private final OrderServicoRepository orderServicoRepository;

    private final OrderServicoMapper orderServicoMapper;

    private final OrderServicoSearchRepository orderServicoSearchRepository;

    public OrderServicoService(
        OrderServicoRepository orderServicoRepository,
        OrderServicoMapper orderServicoMapper,
        OrderServicoSearchRepository orderServicoSearchRepository
    ) {
        this.orderServicoRepository = orderServicoRepository;
        this.orderServicoMapper = orderServicoMapper;
        this.orderServicoSearchRepository = orderServicoSearchRepository;
    }

    /**
     * Save a orderServico.
     *
     * @param orderServicoDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderServicoDTO save(OrderServicoDTO orderServicoDTO) {
        log.debug("Request to save OrderServico : {}", orderServicoDTO);

        //Pega a data e hora atual, status como aberto e salva.
        orderServicoDTO.setDataAbertura(LocalDateTime.now());
        orderServicoDTO.setStatus(Status.ABERTO);

        OrderServico orderServico = orderServicoMapper.toEntity(orderServicoDTO);
        orderServico = orderServicoRepository.save(orderServico);
        OrderServicoDTO result = orderServicoMapper.toDto(orderServico);
        orderServicoSearchRepository.index(orderServico);
        return result;
    }

    /**
     * Update a orderServico.
     *
     * @param orderServicoDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderServicoDTO update(OrderServicoDTO orderServicoDTO) {
        log.debug("Request to update OrderServico : {}", orderServicoDTO);
        OrderServico orderServico = orderServicoMapper.toEntity(orderServicoDTO);
        orderServico = orderServicoRepository.save(orderServico);
        OrderServicoDTO result = orderServicoMapper.toDto(orderServico);
        orderServicoSearchRepository.index(orderServico);
        return result;
    }

    /**
     * Partially update a orderServico.
     *
     * @param orderServicoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderServicoDTO> partialUpdate(OrderServicoDTO orderServicoDTO) {
        log.debug("Request to partially update OrderServico : {}", orderServicoDTO);

        return orderServicoRepository
            .findById(orderServicoDTO.getId())
            .map(existingOrderServico -> {
                orderServicoMapper.partialUpdate(existingOrderServico, orderServicoDTO);

                return existingOrderServico;
            })
            .map(orderServicoRepository::save)
            .map(savedOrderServico -> {
                orderServicoSearchRepository.save(savedOrderServico);

                return savedOrderServico;
            })
            .map(orderServicoMapper::toDto);
    }

    /**
     * Get all the orderServicos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderServicoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrderServicos");
        return orderServicoRepository.findAll(pageable).map(orderServicoMapper::toDto);
    }

    /**
     * Get one orderServico by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public OrderServicoDTO findOne(Long id) {
        log.debug("Request to get OrderServico : {}", id);
        return orderServicoRepository
            .findById(id)
            .map(orderServicoMapper::toDto)
            .orElseThrow(() -> {
            throw new BadRequestAlertException("OS", "Ordem de serviço não encontrado!", "400");
        });
    }

    /**
     * Delete the orderServico by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OrderServico : {}", id);
        orderServicoRepository.deleteById(id);
        orderServicoSearchRepository.deleteById(id);
    }

    /**
     * Search for the orderServico corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderServicoDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OrderServicos for query {}", query);
        return orderServicoSearchRepository.search(query, pageable).map(orderServicoMapper::toDto);
    }
}
