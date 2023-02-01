package org.ujar.jh.petclinic.vuebdd.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.ujar.jh.petclinic.vuebdd.domain.Visits;
import org.ujar.jh.petclinic.vuebdd.repository.rowmapper.PetsRowMapper;
import org.ujar.jh.petclinic.vuebdd.repository.rowmapper.VisitsRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Visits entity.
 */
@SuppressWarnings("unused")
class VisitsRepositoryInternalImpl extends SimpleR2dbcRepository<Visits, Long> implements VisitsRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PetsRowMapper petsMapper;
    private final VisitsRowMapper visitsMapper;

    private static final Table entityTable = Table.aliased("visits", EntityManager.ENTITY_ALIAS);
    private static final Table petTable = Table.aliased("pets", "pet");

    public VisitsRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PetsRowMapper petsMapper,
        VisitsRowMapper visitsMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Visits.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.petsMapper = petsMapper;
        this.visitsMapper = visitsMapper;
    }

    @Override
    public Flux<Visits> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Visits> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = VisitsSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PetsSqlHelper.getColumns(petTable, "pet"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(petTable)
            .on(Column.create("pet_id", entityTable))
            .equals(Column.create("id", petTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Visits.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Visits> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Visits> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Visits process(Row row, RowMetadata metadata) {
        Visits entity = visitsMapper.apply(row, "e");
        entity.setPet(petsMapper.apply(row, "pet"));
        return entity;
    }

    @Override
    public <S extends Visits> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
