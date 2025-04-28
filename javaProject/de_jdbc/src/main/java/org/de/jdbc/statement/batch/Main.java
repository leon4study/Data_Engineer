package org.de.jdbc.statement.batch;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/de-jdbc","root","62206808");
        Statement stmt = con.createStatement();
        stmt.addBatch("SELECT `id`,`name`,`updated_at`, `contents`, `price` from product where id between 1 and 5");
        stmt.addBatch("UPDATE product SET `price` = `price` + 10000 where id = 1");
        // 이렇게만 하면 오류가 나는데
        // Caused by: java.sql.SQLException: Statement.executeUpdate() or Statement.executeLargeUpdate()
        // cannot issue statements that produce result sets.
        // SQL 의 목적이 update, 조작, 생성, 삭제등 데이터 변경이 목적이다.
        // 데이터의 조회는 확인하지도 않을 거 왜 여기다 썼냐는 의미.
        try{
            int[] results = stmt.executeBatch(); // 여기서 executebatch 실행하면 성공/실패 상관 없이 뒤에 executebatch에서 적용되지 않는다.
            // 이미 성공한 쿼리는 롤백하지 않고 살아남을 수 있음.
            // 그래서 아래 코드까지 돌아간 후 확인해보면 id =1 인 아이템이 + 20000 된 것 확인 가능.
        } catch (BatchUpdateException batchUpdateException){
            System.out.println(batchUpdateException.getMessage());
        }

        stmt.addBatch("UPDATE product SET `price` = `price` + 10000 where id = 1");
        stmt.addBatch("UPDATE product SET `price` = `price` + 10000 where id = 2");
        stmt.addBatch("UPDATE product SET `price` = `price` + 10000 where id between 3 and 5");

        int[] results = stmt.executeBatch();
        for (int result : results){
            if (result >= 0){
                System.out.println("result of update : "+ result);
            }
        }

        con.close();
    }
}
