package br.com.rodrigoproject.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import br.com.rodrigoproject.domain.Tecnico;
import br.com.rodrigoproject.repository.TecnicoRepository;
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
 * Spring Data Elasticsearch repository for the {@link Tecnico} entity.
 */
public interface TecnicoSearchRepository extends ElasticsearchRepository<Tecnico, Long>, TecnicoSearchRepositoryInternal {}

interface TecnicoSearchRepositoryInternal {
    Page<Tecnico> search(String query, Pageable pageable);

    Page<Tecnico> search(Query query);

    void index(Tecnico entity);
}

class TecnicoSearchRepositoryInternalImpl implements TecnicoSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TecnicoRepository repository;

    TecnicoSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TecnicoRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Tecnico> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Tecnico> search(Query query) {
        SearchHits<Tecnico> searchHits = elasticsearchTemplate.search(query, Tecnico.class);
        List<Tecnico> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Tecnico entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
