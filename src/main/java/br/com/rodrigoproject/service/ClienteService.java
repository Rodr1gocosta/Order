package br.com.rodrigoproject.service;

import br.com.rodrigoproject.domain.Cliente;
import br.com.rodrigoproject.repository.ClienteRepository;
import br.com.rodrigoproject.repository.search.ClienteSearchRepository;
import br.com.rodrigoproject.service.dto.ClienteDTO;
import br.com.rodrigoproject.service.mapper.ClienteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Cliente}.
 */
@Service
@Transactional
public class ClienteService {

    private final Logger log = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;

    private final ClienteMapper clienteMapper;

    private final ClienteSearchRepository clienteSearchRepository;

    public ClienteService(
        ClienteRepository clienteRepository,
        ClienteMapper clienteMapper,
        ClienteSearchRepository clienteSearchRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.clienteSearchRepository = clienteSearchRepository;
    }

    /**
     * Save a cliente.
     *
     * @param clienteDTO the entity to save.
     * @return the persisted entity.
     */
    public ClienteDTO save(ClienteDTO clienteDTO) {
        log.debug("Request to save Cliente : {}", clienteDTO);

        //Remove os caracteres do CPF
        clienteDTO.setCpf(clienteDTO.getCpf().replaceAll("[^0-9]", ""));

        //Pega a data e hora atual e salva.
        clienteDTO.setDataCriacao(LocalDateTime.now());

        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        cliente = clienteRepository.save(cliente);
        ClienteDTO result = clienteMapper.toDto(cliente);
        clienteSearchRepository.index(cliente);
        return result;
    }

    /**
     * Update a cliente.
     *
     * @param clienteDTO the entity to save.
     * @return the persisted entity.
     */
    public ClienteDTO update(ClienteDTO clienteDTO) {
        log.debug("Request to update Cliente : {}", clienteDTO);
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        // no save call needed as we have no fields that can be updated
        ClienteDTO result = clienteMapper.toDto(cliente);
        clienteSearchRepository.index(cliente);
        return result;
    }

    /**
     * Partially update a cliente.
     *
     * @param clienteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ClienteDTO> partialUpdate(ClienteDTO clienteDTO) {
        log.debug("Request to partially update Cliente : {}", clienteDTO);

        return clienteRepository
            .findById(clienteDTO.getId())
            .map(existingCliente -> {
                clienteMapper.partialUpdate(existingCliente, clienteDTO);

                return existingCliente;
            })
            // .map(clienteRepository::save)
            .map(savedCliente -> {
                clienteSearchRepository.save(savedCliente);

                return savedCliente;
            })
            .map(clienteMapper::toDto);
    }

    /**
     * Get all the clientes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Clientes");
        return clienteRepository.findAll(pageable).map(clienteMapper::toDto);
    }

    /**
     * Get one cliente by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> findOne(Long id) {
        log.debug("Request to get Cliente : {}", id);
        return clienteRepository.findById(id).map(clienteMapper::toDto);
    }

    /**
     * Delete the cliente by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Cliente : {}", id);
        clienteRepository.deleteById(id);
        clienteSearchRepository.deleteById(id);
    }

    /**
     * Search for the cliente corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClienteDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Clientes for query {}", query);
        return clienteSearchRepository.search(query, pageable).map(clienteMapper::toDto);
    }
}
