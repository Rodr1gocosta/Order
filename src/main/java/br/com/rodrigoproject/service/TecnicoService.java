package br.com.rodrigoproject.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import br.com.rodrigoproject.domain.Tecnico;
import br.com.rodrigoproject.repository.TecnicoRepository;
import br.com.rodrigoproject.repository.search.TecnicoSearchRepository;
import br.com.rodrigoproject.service.dto.TecnicoDTO;
import br.com.rodrigoproject.service.mapper.TecnicoMapper;
import java.util.Optional;
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
        Tecnico tecnico = tecnicoMapper.toEntity(tecnicoDTO);
        // no save call needed as we have no fields that can be updated
        TecnicoDTO result = tecnicoMapper.toDto(tecnico);
        tecnicoSearchRepository.index(tecnico);
        return result;
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
    public Optional<TecnicoDTO> findOne(Long id) {
        log.debug("Request to get Tecnico : {}", id);
        return tecnicoRepository.findById(id).map(tecnicoMapper::toDto);
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
