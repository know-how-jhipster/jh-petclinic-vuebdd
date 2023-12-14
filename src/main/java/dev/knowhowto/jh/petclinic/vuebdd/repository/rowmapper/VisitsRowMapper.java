package dev.knowhowto.jh.petclinic.vuebdd.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Visits;

/**
 * Converter between {@link Row} to {@link Visits}, with proper type conversions.
 */
@Service
public class VisitsRowMapper implements BiFunction<Row, String, Visits> {

    private final ColumnConverter converter;

    public VisitsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Visits} stored in the database.
     */
    @Override
    public Visits apply(Row row, String prefix) {
        Visits entity = new Visits();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setVisitdate(converter.fromRow(row, prefix + "_visitdate", Instant.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPetId(converter.fromRow(row, prefix + "_pet_id", Long.class));
        return entity;
    }
}
