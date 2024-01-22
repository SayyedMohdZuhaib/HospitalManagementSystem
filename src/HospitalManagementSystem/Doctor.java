package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Doctor {
    private Connection connection;

    public Doctor(Connection connection){
        this.connection = connection;
    }

    public  void viewDoctor(){
        String query = "SELECT * FROM doctor";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Doctor: ");
            System.out.println("*-----------*--------------------*-------*--------*");
            System.out.println("|Doctor ID  |       NAME         | SPECIALIZATION |");
            System.out.println("*-----------*--------------------*----------------*");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("|%-12s|%-21s|%-17s\n",id, name, specialization);
                System.out.println("*-----------*--------------------*-------*--------*");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Boolean getDoctorById(int id){
        String query = "SELECT * FROM DOCTOR WHERE id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return  true;
            }
            else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
