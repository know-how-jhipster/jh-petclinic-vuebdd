package org.ujar.jh.petclinic.vuebdd.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.ujar.jh.petclinic.vuebdd.domain.Pets;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Pets entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PetsRepository extends ReactiveCrudRepository<Pets, Long>, PetsRepositoryInternal {
    Flux<Pets> findAllBy(Pageable pageable);

    @Query("SELECT * FROM pets entity WHERE entity.type_id = :id")
    Flux<Pets> findByType(Long id);

    @Query("SELECT * FROM pets entity WHERE entity.type_id IS NULL")
    Flux<Pets> findAllWhereTypeIsNull();

    @Query("SELECT * FROM pets entity WHERE entity.owner_id = :id")
    Flux<Pets> findByOwner(Long id);

    @Query("SELECT * FROM pets entity WHERE entity.owner_id IS NULL")
    Flux<Pets> findAllWhereOwnerIsNull();

    @Override
    <S extends Pets> Mono<S> save(S entity);

    @Override
    Flux<Pets> findAll();

    @Override
    Mono<Pets> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PetsRepositoryInternal {
    <S extends Pets> Mono<S> save(S entity);

    Flux<Pets> findAllBy(Pageable pageable);

    Flux<Pets> findAll();

    Mono<Pets> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Pets> findAllBy(Pageable pageable, Criteria criteria);

}
