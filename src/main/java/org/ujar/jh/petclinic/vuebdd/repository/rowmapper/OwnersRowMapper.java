package org.ujar.jh.petclinic.vuebdd.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.ujar.jh.petclinic.vuebdd.domain.Owners;

/**
 * Converter between {@link Row} to {@link Owners}, with proper type conversions.
 */
@Service
public class OwnersRowMapper implements BiFunction<Row, String, Owners> {

    private final ColumnConverter converter;

    public OwnersRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Owners} stored in the database.
     */
    @Override
    public Owners apply(Row row, String prefix) {
        Owners entity = new Owners();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstname(converter.fromRow(row, prefix + "_firstname", String.class));
        entity.setLastname(converter.fromRow(row, prefix + "_lastname", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setTelephone(converter.fromRow(row, prefix + "_telephone", String.class));
        return entity;
    }
}
