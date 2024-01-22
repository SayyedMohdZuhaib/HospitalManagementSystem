package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HotelManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "admin123";


    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            while (true) {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1: Add Patients ");
                System.out.println("2: View Patients ");
                System.out.println("3: View Doctors");
                System.out.println("4: Book Appointment");
                System.out.println("5: View Appointments");
                System.out.println("6: Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        //Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;

                    case 2:
                        //View Patient
                        patient.viewPatient();
                        System.out.println();
                        break;

                    case 3:
                        //View Doctor
                        doctor.viewDoctor();
                        System.out.println();
                        break;

                    case 4:
                        //Book Appointment
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;


                    case 6:
                        System.out.println("!!!!Thank You For using Hospital Management System!!!!");
                        return;

                    case 5:
                        // View Appointments
                        viewAppointments(connection);
                        System.out.println();
                        break;

                    default:
                        System.out.println("Enter Valid choice");
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.println("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date(YYYY-MM-DD)");
        String appointmentDate = scanner.next();
        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "Insert INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowEffected = preparedStatement.executeUpdate();
                    if (rowEffected > 0) {
                        System.out.println("Appointment Booked");
                    } else {
                        System.out.println("Failed to Book Appointment");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("Doctor not available on this date");
            }

        } else {
            System.out.println("Either doctor and patient does not exist");
        }
    }
    public  static boolean checkDoctorAvailability(int doctorId , String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments where doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        }
        catch(SQLException e){
            e.printStackTrace();

        }
        return false;
    }

    public static void viewAppointments(Connection connection) {
        System.out.println("Appointments: ");
        System.out.println("*-----------------*------------------*------------------*-----------------*------------------|");
        System.out.println("| Appointment ID  |  Patient Name    |  Doctor Name     |  Specialization | Appointment Date |");
        System.out.println("*-----------------*------------------*------------------*-----------------*------------------|");

        String query = "SELECT appointments.id, patients.name as patient_name, doctor.name as doctor_name, " +
                "doctor.specialization, appointments.appointment_date " +
                "FROM appointments " +
                "INNER JOIN patients ON appointments.patient_id = patients.id " +
                "INNER JOIN doctor ON appointments.doctor_id = doctor.id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("id");
                String patientName = resultSet.getString("patient_name");
                String doctorName = resultSet.getString("doctor_name");
                String specialization = resultSet.getString("specialization");
                String appointmentDate = resultSet.getString("appointment_date");

                System.out.printf("|%-18s|%-18s|%-18s|%-17s|%-18s\n",
                        appointmentId, patientName, doctorName, specialization, appointmentDate);
                System.out.println("*-----------------*------------------*------------------*-----------------*------------------|");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception more gracefully
        }
    }


}



