package dev.knowhowto.jh.petclinic.vuebdd.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Visits;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Visits entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VisitsRepository extends ReactiveCrudRepository<Visits, Long>, VisitsRepositoryInternal {
    Flux<Visits> findAllBy(Pageable pageable);

    @Query("SELECT * FROM visits entity WHERE entity.pet_id = :id")
    Flux<Visits> findByPet(Long id);

    @Query("SELECT * FROM visits entity WHERE entity.pet_id IS NULL")
    Flux<Visits> findAllWherePetIsNull();

    @Override
    <S extends Visits> Mono<S> save(S entity);

    @Override
    Flux<Visits> findAll();

    @Override
    Mono<Visits> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface VisitsRepositoryInternal {
    <S extends Visits> Mono<S> save(S entity);

    Flux<Visits> findAllBy(Pageable pageable);

    Flux<Visits> findAll();

    Mono<Visits> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Visits> findAllBy(Pageable pageable, Criteria criteria);

}
