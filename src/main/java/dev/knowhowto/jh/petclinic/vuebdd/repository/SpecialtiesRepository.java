package dev.knowhowto.jh.petclinic.vuebdd.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Specialties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Specialties entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpecialtiesRepository extends ReactiveCrudRepository<Specialties, Long>, SpecialtiesRepositoryInternal {
    @Override
    Mono<Specialties> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Specialties> findAllWithEagerRelationships();

    @Override
    Flux<Specialties> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM specialties entity JOIN rel_specialties__vet joinTable ON entity.id = joinTable.vet_id WHERE joinTable.vet_id = :id"
    )
    Flux<Specialties> findByVet(Long id);

    @Override
    <S extends Specialties> Mono<S> save(S entity);

    @Override
    Flux<Specialties> findAll();

    @Override
    Mono<Specialties> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SpecialtiesRepositoryInternal {
    <S extends Specialties> Mono<S> save(S entity);

    Flux<Specialties> findAllBy(Pageable pageable);

    Flux<Specialties> findAll();

    Mono<Specialties> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Specialties> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Specialties> findOneWithEagerRelationships(Long id);

    Flux<Specialties> findAllWithEagerRelationships();

    Flux<Specialties> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
