package org.de.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetMapper {
    public static Product create(ResultSet rs) throws SQLException {
        return new Product(
        rs.getInt(1), rs.getString(2),
                rs.getTimestamp(3).toLocalDateTime(),rs.getString(4),
                rs.getInt(5));
    }

    public static void printRs (ResultSet rs) throws SQLException {
        System.out.println(rs.getInt(1) + " " + rs.getString(2) + " "
                + rs.getDate(3) + " " + rs.getString(4)
                + " " + rs.getInt(5));
    }
}
