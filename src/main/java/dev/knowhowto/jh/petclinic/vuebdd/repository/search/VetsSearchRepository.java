package dev.knowhowto.jh.petclinic.vuebdd.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Vets;
import dev.knowhowto.jh.petclinic.vuebdd.repository.VetsRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Vets} entity.
 */
public interface VetsSearchRepository extends ReactiveElasticsearchRepository<Vets, Long>, VetsSearchRepositoryInternal {}

interface VetsSearchRepositoryInternal {
    Flux<Vets> search(String query, Pageable pageable);

    Flux<Vets> search(Query query);
}

class VetsSearchRepositoryInternalImpl implements VetsSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    VetsSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Vets> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<Vets> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Vets.class).map(SearchHit::getContent);
    }
}
