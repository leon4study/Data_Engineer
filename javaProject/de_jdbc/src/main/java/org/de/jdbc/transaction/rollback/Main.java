package org.de.jdbc.transaction.rollback;

import org.de.jdbc.mapper.ResultSetMapper;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/de-jdbc","root","62206808");
        try{
            con.setAutoCommit(false);
            PreparedStatement updateStmt = con.prepareStatement("UPDATE product SET price = price + 500 where id  = ?");
            PreparedStatement selectStmt = con.prepareStatement("SELECT `id`,`name`,`updated_at`, `contents`, `price` from product where id = ?");

            System.out.println("===== Before Start Update ==== ");
            selectAndPrintRow(selectStmt, 2);
            selectAndPrintRow(selectStmt, 3);
            selectAndPrintRow(selectStmt, 4);

            updateStmt.setInt(1, 2);
            updateStmt.executeUpdate();
            updateStmt.setInt(1, 3);
            updateStmt.executeUpdate();
            updateStmt.setInt(1, 4);
            updateStmt.executeUpdate();

            System.out.println("======= After Update in Transaction ========");
            selectAndPrintRow(selectStmt, 2);
            selectAndPrintRow(selectStmt, 3);
            selectAndPrintRow(selectStmt, 4);

            con.rollback();

            System.out.println("======= After Rollback in Transaction ========");
            selectAndPrintRow(selectStmt, 2);
            selectAndPrintRow(selectStmt, 3);
            selectAndPrintRow(selectStmt, 4);

        }catch (SQLException sqlException){
            System.out.println(sqlException.getErrorCode()+ ", "+sqlException.getMessage());
        }finally  {
            con.close();
        }
    }
    private static void selectAndPrintRow(PreparedStatement stmt, int id ) throws SQLException {
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()){
            ResultSetMapper.printRs(rs);
        }
    }
}
