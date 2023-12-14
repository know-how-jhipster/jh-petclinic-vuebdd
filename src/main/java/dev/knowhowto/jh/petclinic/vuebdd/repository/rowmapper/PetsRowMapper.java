package dev.knowhowto.jh.petclinic.vuebdd.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import dev.knowhowto.jh.petclinic.vuebdd.domain.Pets;

/**
 * Converter between {@link Row} to {@link Pets}, with proper type conversions.
 */
@Service
public class PetsRowMapper implements BiFunction<Row, String, Pets> {

    private final ColumnConverter converter;

    public PetsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pets} stored in the database.
     */
    @Override
    public Pets apply(Row row, String prefix) {
        Pets entity = new Pets();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setBirthdate(converter.fromRow(row, prefix + "_birthdate", LocalDate.class));
        entity.setTypeId(converter.fromRow(row, prefix + "_type_id", Long.class));
        entity.setOwnerId(converter.fromRow(row, prefix + "_owner_id", Long.class));
        return entity;
    }
}
