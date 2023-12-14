package dev.knowhowto.jh.petclinic.vuebdd.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Vets;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Vets entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VetsRepository extends ReactiveCrudRepository<Vets, Long>, VetsRepositoryInternal {
    Flux<Vets> findAllBy(Pageable pageable);

    @Override
    <S extends Vets> Mono<S> save(S entity);

    @Override
    Flux<Vets> findAll();

    @Override
    Mono<Vets> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface VetsRepositoryInternal {
    <S extends Vets> Mono<S> save(S entity);

    Flux<Vets> findAllBy(Pageable pageable);

    Flux<Vets> findAll();

    Mono<Vets> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Vets> findAllBy(Pageable pageable, Criteria criteria);

}
