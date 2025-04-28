package org.de.jdbc.crudExample;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try {
            //here de-jdbc is database name, root is username and password is null. Fix them for your database settings.
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/de-jdbc", "root", "62206808");
            Connection con2 = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/de-jdbc", "root", "62206808");

            // 이렇게 인터페이스가 있음에도 결과가 전달되지 않은 값들이 있을 수 있다.
            // DB 드라이버를 어떻게 구현하느냐에 따라 달려있기 때문에 표준인터페이스라고 하더라도 결과가 나오지 않을 수 있다.
            // DatabaseMetaData 인터페이스에 있는 녀석들만 믿지 말고 실제로 여기서 코딩하데 필요하다면
            // 실제 내가 쓰는 DB에 잘 넘어오는지 확인할 필요가 있다.
            // 예) 버전 따라 달라지는 기능 등.
            DatabaseMetaData databaseMetaData = con.getMetaData();
            System.out.println(databaseMetaData.getDriverName() + " " + databaseMetaData.getDriverVersion());


            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from product");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " "
                        + rs.getDate(3) + " " + rs.getString(4)
                        + " " + rs.getInt(5));
            }
            Thread.sleep(5*60*1000);
            con.close();
        } catch (Exception e) {System.out.println(e);}
    }
}
