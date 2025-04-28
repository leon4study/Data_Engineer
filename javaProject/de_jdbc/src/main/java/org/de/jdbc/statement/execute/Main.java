package org.de.jdbc.statement.execute;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/de-jdbc","root","62206808");
        Statement stmt = con.createStatement();
        boolean selectResult =  stmt.execute("SELECT `id`,`name`,`updated_at`, `contents`, `price` from product where id between 1 and 5");

        if (selectResult) {
            ResultSet resultSet = stmt.getResultSet();
            while (resultSet.next()){
                printRs(resultSet);
            }
        }

        boolean updateResult = stmt.execute("UPDATE product SET `price` = `price` + 10000 where id = 1");
        if (!updateResult){
            System.out.println("result of update : " + stmt.getUpdateCount());
        }



    }

    private static void printRs (ResultSet rs) throws SQLException {
        System.out.println(rs.getInt(1) + " " + rs.getString(2) + " "
                + rs.getDate(3) + " " + rs.getString(4)
                + " " + rs.getInt(5));
    }
}
