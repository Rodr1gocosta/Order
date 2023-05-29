package br.com.rodrigoproject.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import br.com.rodrigoproject.domain.OrderServico;
import br.com.rodrigoproject.repository.OrderServicoRepository;
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
 * Spring Data Elasticsearch repository for the {@link OrderServico} entity.
 */
public interface OrderServicoSearchRepository extends ElasticsearchRepository<OrderServico, Long>, OrderServicoSearchRepositoryInternal {}

interface OrderServicoSearchRepositoryInternal {
    Page<OrderServico> search(String query, Pageable pageable);

    Page<OrderServico> search(Query query);

    void index(OrderServico entity);
}

class OrderServicoSearchRepositoryInternalImpl implements OrderServicoSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final OrderServicoRepository repository;

    OrderServicoSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, OrderServicoRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<OrderServico> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<OrderServico> search(Query query) {
        SearchHits<OrderServico> searchHits = elasticsearchTemplate.search(query, OrderServico.class);
        List<OrderServico> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(OrderServico entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
