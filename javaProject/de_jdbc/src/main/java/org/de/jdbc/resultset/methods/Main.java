package org.de.jdbc.resultset.methods;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/de-jdbc","root","62206808");
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)  ;
        ResultSet rs = stmt.executeQuery(
                "SELECT `id`,`name`,`updated_at`, `contents`, `price` from product where id between 1 and 5");

        //cursor init
        if (rs.next()){
            // cursor
            printRs(rs);
        }
        if (rs.last()){
            printRs(rs);
        }
        if (!rs.next()){
            try{
                printRs(rs);// After end of result set error
            } catch (SQLException e) {
                System.out.println(e.getErrorCode() + ", " +e.getMessage());
            }
        }
        rs.last();
        if (rs.previous()){
            printRs(rs);
        }
        if (rs.absolute(2)){
            printRs(rs);
        }
        if (rs.relative(2)){
            printRs(rs);
        }
        if (rs.relative(-3)){
            printRs(rs);
        }
        if (rs.first()){
            printRs(rs);
        }
        if(rs.previous()){
            try{
                printRs(rs); // before start of result set error
            } catch (SQLException e) {
                System.out.println(e.getErrorCode() + ", " +e.getMessage());
            }
        }
        con.close();
    }

    private static void printRs (ResultSet rs) throws SQLException {
        System.out.println(rs.getInt(1) + " " + rs.getString(2) + " "
                + rs.getDate(3) + " " + rs.getString(4)
                + " " + rs.getInt(5));
    }

}
