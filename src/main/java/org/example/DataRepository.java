package org.example;

import java.sql.*;
import java.time.LocalDate;

public class DataRepository {

    Connection con;

    public DataRepository() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/weather","root","Kittu@96");
    }
    public void saveInfo(double[] tempStats, String[] geo, String condition) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into temperature values( ?, ?, ?, ?, ?, ?)");
        ps.setString(1 , LocalDate.now().toString());
        ps.setString(2 , geo[2]);
        ps.setDouble(3 , tempStats[0]);
        ps.setDouble(4 , tempStats[1]);
        double avg = tempStats[2]/tempStats[3];
        ps.setDouble(5, avg);
        ps.setString(6,condition);
        int rs = ps.executeUpdate();

        if(rs == 1) System.out.println("Data has been uploaded successfully");
        else System.out.println("Unexpected error occurred while uploading the data.");

    }
}
