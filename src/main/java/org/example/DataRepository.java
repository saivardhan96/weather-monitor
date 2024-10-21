package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class DataRepository {

    Connection con;

    public DataRepository() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/weather","root","Kittu@96");
    }
    public void saveInfo(double[] tempStats, String[] geo, String condition, int unit) throws SQLException {
        tempStats[2] = tempStats[2]/tempStats[3];
        tempConverter(unit,tempStats);
        PreparedStatement ps = con.prepareStatement("insert into temperature values( ?, ?, ?, ?, ?, ?)");
        ps.setString(1 , LocalDate.now().toString());
        ps.setString(2 , geo[2]);
        ps.setDouble(3 , tempStats[0]);
        ps.setDouble(4 , tempStats[1]);
        ps.setDouble(5, tempStats[2]);
        ps.setString(6,condition);
        int rs = ps.executeUpdate();

        if(rs == 1) System.out.println("Data has been uploaded successfully");
        else System.out.println("Unexpected error occurred while uploading the data.");
    }

    private void tempConverter(int unit, double[] temp){
        if(unit == 2){
            temp[0] = 5*(temp[0]-32) / 9.0;
            temp[1] = 5*(temp[1]-32) / 9.0;
            temp[2] = 5*(temp[2]-32) / 9.0;
        }
        else if(unit == 3){
            temp[0] -=273.15;
            temp[1] -=273.15;
            temp[2] -=273.15;
        }
    }
}
