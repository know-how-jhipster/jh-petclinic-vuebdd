package org.ujar.jh.petclinic.vuebdd.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PetsSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("birthdate", table, columnPrefix + "_birthdate"));

        columns.add(Column.aliased("type_id", table, columnPrefix + "_type_id"));
        columns.add(Column.aliased("owner_id", table, columnPrefix + "_owner_id"));
        return columns;
    }
}
