package br.com.rodrigoproject.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import br.com.rodrigoproject.repository.OrderServicoRepository;
import br.com.rodrigoproject.service.OrderServicoService;
import br.com.rodrigoproject.service.dto.OrderServicoDTO;
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
 * REST controller for managing {@link br.com.rodrigoproject.domain.OrderServico}.
 */
@RestController
@RequestMapping("/api")
public class OrderServicoResource {

    private final Logger log = LoggerFactory.getLogger(OrderServicoResource.class);

    private static final String ENTITY_NAME = "orderOrderServico";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderServicoService orderServicoService;

    private final OrderServicoRepository orderServicoRepository;

    public OrderServicoResource(OrderServicoService orderServicoService, OrderServicoRepository orderServicoRepository) {
        this.orderServicoService = orderServicoService;
        this.orderServicoRepository = orderServicoRepository;
    }

    /**
     * {@code POST  /order-servicos} : Create a new orderServico.
     *
     * @param orderServicoDTO the orderServicoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderServicoDTO, or with status {@code 400 (Bad Request)} if the orderServico has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-servicos")
    public ResponseEntity<OrderServicoDTO> createOrderServico(@RequestBody OrderServicoDTO orderServicoDTO) throws URISyntaxException {
        log.debug("REST request to save OrderServico : {}", orderServicoDTO);
        if (orderServicoDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderServico cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderServicoDTO result = orderServicoService.save(orderServicoDTO);
        return ResponseEntity
            .created(new URI("/api/order-servicos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-servicos/:id} : Updates an existing orderServico.
     *
     * @param id the id of the orderServicoDTO to save.
     * @param orderServicoDTO the orderServicoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderServicoDTO,
     * or with status {@code 400 (Bad Request)} if the orderServicoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderServicoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-servicos/{id}")
    public ResponseEntity<OrderServicoDTO> updateOrderServico(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderServicoDTO orderServicoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrderServico : {}, {}", id, orderServicoDTO);
        if (orderServicoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderServicoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderServicoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderServicoDTO result = orderServicoService.update(orderServicoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderServicoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /order-servicos/:id} : Partial updates given fields of an existing orderServico, field will ignore if it is null
     *
     * @param id the id of the orderServicoDTO to save.
     * @param orderServicoDTO the orderServicoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderServicoDTO,
     * or with status {@code 400 (Bad Request)} if the orderServicoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderServicoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderServicoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-servicos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderServicoDTO> partialUpdateOrderServico(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderServicoDTO orderServicoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderServico partially : {}, {}", id, orderServicoDTO);
        if (orderServicoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderServicoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderServicoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderServicoDTO> result = orderServicoService.partialUpdate(orderServicoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderServicoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-servicos} : get all the orderServicos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderServicos in body.
     */
    @GetMapping("/order-servicos")
    public ResponseEntity<List<OrderServicoDTO>> getAllOrderServicos(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of OrderServicos");
        Page<OrderServicoDTO> page = orderServicoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /order-servicos/:id} : get the "id" orderServico.
     *
     * @param id the id of the orderServicoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderServicoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-servicos/{id}")
    public ResponseEntity<OrderServicoDTO> getOrderServico(@PathVariable Long id) {
        log.debug("REST request to get OrderServico : {}", id);
        Optional<OrderServicoDTO> orderServicoDTO = orderServicoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderServicoDTO);
    }

    /**
     * {@code DELETE  /order-servicos/:id} : delete the "id" orderServico.
     *
     * @param id the id of the orderServicoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-servicos/{id}")
    public ResponseEntity<Void> deleteOrderServico(@PathVariable Long id) {
        log.debug("REST request to delete OrderServico : {}", id);
        orderServicoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/order-servicos?query=:query} : search for the orderServico corresponding
     * to the query.
     *
     * @param query the query of the orderServico search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/order-servicos")
    public ResponseEntity<List<OrderServicoDTO>> searchOrderServicos(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of OrderServicos for query {}", query);
        Page<OrderServicoDTO> page = orderServicoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
