package org.de.jdbc.callablestatement.function;

import org.de.jdbc.mapper.ResultSetMapper;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/de-jdbc", "root", "62206808");
        CallableStatement stmtFunctionCall = con.prepareCall("{? = call add_event_prefix(?)}");
        String originalContent = "original";
        System.out.println("Original content" + originalContent);
        stmtFunctionCall.setString(2, originalContent);
        stmtFunctionCall.registerOutParameter(1, Types.VARCHAR);
        boolean result2 = stmtFunctionCall.execute();
        System.out.println(result2);
        System.out.println("After Prefix : " + stmtFunctionCall.getString(1));
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT `id`,add_event_prefix(`name`) as `name`, `updated_at`, `contents`, `price` from product where id =1;");

        while (rs.next()){
            ResultSetMapper.printRs(rs);
        }
    }
}
