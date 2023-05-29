package br.com.rodrigoproject.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import br.com.rodrigoproject.repository.TecnicoRepository;
import br.com.rodrigoproject.service.TecnicoService;
import br.com.rodrigoproject.service.dto.TecnicoDTO;
import br.com.rodrigoproject.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link br.com.rodrigoproject.domain.Tecnico}.
 */
@RestController
@RequestMapping("/api")
public class TecnicoResource {

    private final Logger log = LoggerFactory.getLogger(TecnicoResource.class);

    private static final String ENTITY_NAME = "orderTecnico";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TecnicoService tecnicoService;

    private final TecnicoRepository tecnicoRepository;

    public TecnicoResource(TecnicoService tecnicoService, TecnicoRepository tecnicoRepository) {
        this.tecnicoService = tecnicoService;
        this.tecnicoRepository = tecnicoRepository;
    }

    /**
     * {@code POST  /tecnicos} : Create a new tecnico.
     *
     * @param tecnicoDTO the tecnicoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tecnicoDTO, or with status {@code 400 (Bad Request)} if the tecnico has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tecnicos")
    public ResponseEntity<TecnicoDTO> createTecnico(@RequestBody TecnicoDTO tecnicoDTO) throws URISyntaxException {
        log.debug("REST request to save Tecnico : {}", tecnicoDTO);
        if (tecnicoDTO.getId() != null) {
            throw new BadRequestAlertException("A new tecnico cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TecnicoDTO result = tecnicoService.save(tecnicoDTO);
        return ResponseEntity
            .created(new URI("/api/tecnicos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tecnicos/:id} : Updates an existing tecnico.
     *
     * @param id the id of the tecnicoDTO to save.
     * @param tecnicoDTO the tecnicoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tecnicoDTO,
     * or with status {@code 400 (Bad Request)} if the tecnicoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tecnicoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tecnicos/{id}")
    public ResponseEntity<TecnicoDTO> updateTecnico(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TecnicoDTO tecnicoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Tecnico : {}, {}", id, tecnicoDTO);
        if (tecnicoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tecnicoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tecnicoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TecnicoDTO result = tecnicoService.update(tecnicoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tecnicoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tecnicos/:id} : Partial updates given fields of an existing tecnico, field will ignore if it is null
     *
     * @param id the id of the tecnicoDTO to save.
     * @param tecnicoDTO the tecnicoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tecnicoDTO,
     * or with status {@code 400 (Bad Request)} if the tecnicoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tecnicoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tecnicoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tecnicos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TecnicoDTO> partialUpdateTecnico(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TecnicoDTO tecnicoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tecnico partially : {}, {}", id, tecnicoDTO);
        if (tecnicoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tecnicoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tecnicoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TecnicoDTO> result = tecnicoService.partialUpdate(tecnicoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tecnicoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tecnicos} : get all the tecnicos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tecnicos in body.
     */
    @GetMapping("/tecnicos")
    public ResponseEntity<List<TecnicoDTO>> getAllTecnicos(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Tecnicos");
        Page<TecnicoDTO> page = tecnicoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tecnicos/:id} : get the "id" tecnico.
     *
     * @param id the id of the tecnicoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tecnicoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tecnicos/{id}")
    public ResponseEntity<TecnicoDTO> getTecnico(@PathVariable Long id) {
        log.debug("REST request to get Tecnico : {}", id);
        Optional<TecnicoDTO> tecnicoDTO = tecnicoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tecnicoDTO);
    }

    /**
     * {@code DELETE  /tecnicos/:id} : delete the "id" tecnico.
     *
     * @param id the id of the tecnicoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tecnicos/{id}")
    public ResponseEntity<Void> deleteTecnico(@PathVariable Long id) {
        log.debug("REST request to delete Tecnico : {}", id);
        tecnicoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/tecnicos?query=:query} : search for the tecnico corresponding
     * to the query.
     *
     * @param query the query of the tecnico search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/tecnicos")
    public ResponseEntity<List<TecnicoDTO>> searchTecnicos(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Tecnicos for query {}", query);
        Page<TecnicoDTO> page = tecnicoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
