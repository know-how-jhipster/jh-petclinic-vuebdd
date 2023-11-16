package dev.knowhowto.jh.petclinic.vuebdd.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Specialties;
import dev.knowhowto.jh.petclinic.vuebdd.repository.SpecialtiesRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Specialties} entity.
 */
public interface SpecialtiesSearchRepository
    extends ReactiveElasticsearchRepository<Specialties, Long>, SpecialtiesSearchRepositoryInternal {}

interface SpecialtiesSearchRepositoryInternal {
    Flux<Specialties> search(String query);

    Flux<Specialties> search(Query query);
}

class SpecialtiesSearchRepositoryInternalImpl implements SpecialtiesSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    SpecialtiesSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Specialties> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<Specialties> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Specialties.class).map(SearchHit::getContent);
    }
}
