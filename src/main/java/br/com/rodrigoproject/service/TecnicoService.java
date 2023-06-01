package br.com.rodrigoproject.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import br.com.rodrigoproject.domain.Tecnico;
import br.com.rodrigoproject.repository.TecnicoRepository;
import br.com.rodrigoproject.repository.search.TecnicoSearchRepository;
import br.com.rodrigoproject.service.dto.ClienteDTO;
import br.com.rodrigoproject.service.dto.TecnicoDTO;
import br.com.rodrigoproject.service.mapper.TecnicoMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import br.com.rodrigoproject.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Tecnico}.
 */
@Service
@Transactional
public class TecnicoService {

    private final Logger log = LoggerFactory.getLogger(TecnicoService.class);

    private final TecnicoRepository tecnicoRepository;

    private final TecnicoMapper tecnicoMapper;

    private final TecnicoSearchRepository tecnicoSearchRepository;

    public TecnicoService(
        TecnicoRepository tecnicoRepository,
        TecnicoMapper tecnicoMapper,
        TecnicoSearchRepository tecnicoSearchRepository
    ) {
        this.tecnicoRepository = tecnicoRepository;
        this.tecnicoMapper = tecnicoMapper;
        this.tecnicoSearchRepository = tecnicoSearchRepository;
    }

    /**
     * Save a tecnico.
     *
     * @param tecnicoDTO the entity to save.
     * @return the persisted entity.
     */
    public TecnicoDTO save(TecnicoDTO tecnicoDTO) {
        log.debug("Request to save Tecnico : {}", tecnicoDTO);

        validaCpf(tecnicoDTO);

        //Pega a data e hora atual e salva.
        tecnicoDTO.setDataCriacao(LocalDateTime.now());

        Tecnico tecnico = tecnicoMapper.toEntity(tecnicoDTO);
        tecnico = tecnicoRepository.save(tecnico);
        TecnicoDTO result = tecnicoMapper.toDto(tecnico);
        tecnicoSearchRepository.index(tecnico);
        return result;
    }

    /**
     * Update a tecnico.
     *
     * @param tecnicoDTO the entity to save.
     * @return the persisted entity.
     */
    public TecnicoDTO update(TecnicoDTO tecnicoDTO) {
        log.debug("Request to update Tecnico : {}", tecnicoDTO);

        TecnicoDTO findTecnico = findOne(tecnicoDTO.getId());

        validaCpf(tecnicoDTO, findTecnico);

        tecnicoDTO.setId(findTecnico.getId());
        tecnicoDTO.setDataCriacao(findTecnico.getDataCriacao());

        Tecnico tecnico = tecnicoMapper.toEntity(tecnicoDTO);
        tecnico = tecnicoRepository.save(tecnico);
        TecnicoDTO result = tecnicoMapper.toDto(tecnico);
        tecnicoSearchRepository.index(tecnico);
        return result;
    }

    private void validaCpf(TecnicoDTO tecnicoDTO) {
        //Remove os caracteres do CPF
        tecnicoDTO.setCpf(tecnicoDTO.getCpf().replaceAll("[^0-9]", ""));

        // Verifica se o CPF já existe na base de dados
        if (cpfAlreadyExists(tecnicoDTO.getCpf())) {
            throw new BadRequestAlertException("TECNICO", "Tecnico", "CPF existe na base de dados!");
        }
    }

    private void validaCpf(TecnicoDTO tecnicoDTO, TecnicoDTO findTecnico) {
        //Remove os caracteres do CPF
        tecnicoDTO.setCpf(tecnicoDTO.getCpf().replaceAll("[^0-9]", ""));

        if (!tecnicoDTO.getCpf().equals(findTecnico.getCpf()) && cpfAlreadyExists(tecnicoDTO.getCpf())) {
            throw new BadRequestAlertException("TECNICO", "Tecnico", "CPF existe na base de dados!");
        }
    }

    private boolean cpfAlreadyExists(String cpf) {
        return tecnicoRepository.existsByCpf(cpf);
    }

    /**
     * Partially update a tecnico.
     *
     * @param tecnicoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TecnicoDTO> partialUpdate(TecnicoDTO tecnicoDTO) {
        log.debug("Request to partially update Tecnico : {}", tecnicoDTO);

        return tecnicoRepository
            .findById(tecnicoDTO.getId())
            .map(existingTecnico -> {
                tecnicoMapper.partialUpdate(existingTecnico, tecnicoDTO);

                return existingTecnico;
            })
            // .map(tecnicoRepository::save)
            .map(savedTecnico -> {
                tecnicoSearchRepository.save(savedTecnico);

                return savedTecnico;
            })
            .map(tecnicoMapper::toDto);
    }

    /**
     * Get all the tecnicos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TecnicoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tecnicos");
        return tecnicoRepository.findAll(pageable).map(tecnicoMapper::toDto);
    }

    /**
     * Get one tecnico by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public TecnicoDTO findOne(Long id) {
        log.debug("Request to get Tecnico : {}", id);
        return tecnicoRepository
            .findById(id)
            .map(tecnicoMapper::toDto)
            .orElseThrow(() -> {
            throw new BadRequestAlertException("TECNICO", "Tecnico", "Técnico não encontrado!");
        });
    }

    /**
     * Delete the tecnico by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Tecnico : {}", id);
        tecnicoRepository.deleteById(id);
        tecnicoSearchRepository.deleteById(id);
    }

    /**
     * Search for the tecnico corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TecnicoDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tecnicos for query {}", query);
        return tecnicoSearchRepository.search(query, pageable).map(tecnicoMapper::toDto);
    }
}
