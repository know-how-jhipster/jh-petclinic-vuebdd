package dev.knowhowto.jh.petclinic.vuebdd.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Owners;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Owners entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OwnersRepository extends ReactiveCrudRepository<Owners, Long>, OwnersRepositoryInternal {
    Flux<Owners> findAllBy(Pageable pageable);

    @Override
    <S extends Owners> Mono<S> save(S entity);

    @Override
    Flux<Owners> findAll();

    @Override
    Mono<Owners> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OwnersRepositoryInternal {
    <S extends Owners> Mono<S> save(S entity);

    Flux<Owners> findAllBy(Pageable pageable);

    Flux<Owners> findAll();

    Mono<Owners> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Owners> findAllBy(Pageable pageable, Criteria criteria);

}
