package org.ujar.jh.petclinic.vuebdd.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.ujar.jh.petclinic.vuebdd.domain.Specialties;
import org.ujar.jh.petclinic.vuebdd.domain.Vets;
import org.ujar.jh.petclinic.vuebdd.repository.rowmapper.SpecialtiesRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Specialties entity.
 */
@SuppressWarnings("unused")
class SpecialtiesRepositoryInternalImpl extends SimpleR2dbcRepository<Specialties, Long> implements SpecialtiesRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SpecialtiesRowMapper specialtiesMapper;

    private static final Table entityTable = Table.aliased("specialties", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable vetLink = new EntityManager.LinkTable("rel_specialties__vet", "specialties_id", "vet_id");

    public SpecialtiesRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SpecialtiesRowMapper specialtiesMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Specialties.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.specialtiesMapper = specialtiesMapper;
    }

    @Override
    public Flux<Specialties> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Specialties> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = SpecialtiesSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Specialties.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Specialties> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Specialties> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Specialties> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Specialties> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Specialties> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Specialties process(Row row, RowMetadata metadata) {
        Specialties entity = specialtiesMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Specialties> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Specialties> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(vetLink, entity.getId(), entity.getVets().stream().map(Vets::getId)).then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(vetLink, entityId);
    }
}
