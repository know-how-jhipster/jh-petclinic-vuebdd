package org.ujar.jh.petclinic.vuebdd.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.ujar.jh.petclinic.vuebdd.domain.Vets;

/**
 * Converter between {@link Row} to {@link Vets}, with proper type conversions.
 */
@Service
public class VetsRowMapper implements BiFunction<Row, String, Vets> {

    private final ColumnConverter converter;

    public VetsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Vets} stored in the database.
     */
    @Override
    public Vets apply(Row row, String prefix) {
        Vets entity = new Vets();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstname(converter.fromRow(row, prefix + "_firstname", String.class));
        entity.setLastname(converter.fromRow(row, prefix + "_lastname", String.class));
        return entity;
    }
}
