package br.com.rodrigoproject.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import br.com.rodrigoproject.domain.Cliente;
import br.com.rodrigoproject.repository.ClienteRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Cliente} entity.
 */
public interface ClienteSearchRepository extends ElasticsearchRepository<Cliente, Long>, ClienteSearchRepositoryInternal {}

interface ClienteSearchRepositoryInternal {
    Page<Cliente> search(String query, Pageable pageable);

    Page<Cliente> search(Query query);

    void index(Cliente entity);
}

class ClienteSearchRepositoryInternalImpl implements ClienteSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ClienteRepository repository;

    ClienteSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ClienteRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Cliente> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Cliente> search(Query query) {
        SearchHits<Cliente> searchHits = elasticsearchTemplate.search(query, Cliente.class);
        List<Cliente> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Cliente entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}