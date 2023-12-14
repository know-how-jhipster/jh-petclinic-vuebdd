package dev.knowhowto.jh.petclinic.vuebdd.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Types;

/**
 * Converter between {@link Row} to {@link Types}, with proper type conversions.
 */
@Service
public class TypesRowMapper implements BiFunction<Row, String, Types> {

    private final ColumnConverter converter;

    public TypesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Types} stored in the database.
     */
    @Override
    public Types apply(Row row, String prefix) {
        Types entity = new Types();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        return entity;
    }
}
