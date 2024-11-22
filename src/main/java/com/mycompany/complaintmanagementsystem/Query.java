/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.complaintmanagementsystem;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
        /**
 *
 * @author tushar sharma
 */
public class Query {
    
    public static Admin currentAdmin;  // Static variable for admin details
    public static User currentUser;    // Static variable for user details
    private static final String URL = "jdbc:mysql://localhost:3306/student";
    private static final String USER = "root";
    private static final String PASSWORD = "Tushar@123";
    private static Connection connection;
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
}
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close database connection: " + e.getMessage());
            }
        }
    }
    public static ResultSet AdminLogin(String adminname, String password) throws SQLException {
        String query = "SELECT * FROM admin WHERE email = ? AND password = ?";
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
             
        preparedStatement.setString(1, adminname);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        if (resultSet.next()) {
        // If record found, store admin details in static variable
        int id = resultSet.getInt("id");
        String email = resultSet.getString("email");
        String name = resultSet.getString("name");

        // Create Admin object and store in GlobalVariables
        Query.currentAdmin = new Admin(id, email, name);

        System.out.println("Admin logged in: " + Query.currentAdmin.getName());
    } else {
        System.out.println("Invalid admin credentials.");
    }
            
        return resultSet; // Return true if a record is found

        }
    public static User UserLogin(String username, String password) throws SQLException {
        String query = "SELECT * FROM student WHERE Email = ? AND Password = ?";
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
             
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        if (resultSet.next()) {
        // If record found, store user details in static variable
        String email = resultSet.getString("Email");
        String name = resultSet.getString("name");
        String Branch = resultSet.getString("Branch");

        // Create User object and store in GlobalVariables
        Query.currentUser = new User(email, name,Branch);

        System.out.println("User logged in: " + Query.currentUser.getName());
    } else {
        System.out.println("Invalid user credentials.");
    }
            
        return currentUser; // Return true if a record is found

        }
    public static void loadTableData(DefaultTableModel model, String query) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing data and set column names
            model.setRowCount(0);
            model.setColumnCount(0);
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            // Populate rows
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error loading table data: " + e.getMessage());
        }
    }
     public static int loadNumberofComplaints(){
         try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ) {
        String query = "SELECT COUNT(*) AS total_complaints FROM complaints"; 
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
        int totalComplaints = rs.getInt("total_complaints");
        System.out.println("Total Complaints: " + totalComplaints);
        return totalComplaints;       
     }
     }  catch (SQLException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
     }
     
     public static int pendingcomplaints(){
         try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ) {
        String query = "SELECT COUNT(*) AS pending_complaints FROM complaints where status='SUBMITTED'"; 
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
        int pendingComplaints = rs.getInt("pending_complaints");
        System.out.println("Total Complaints: " + pendingComplaints);
        return pendingComplaints;       
     }
     }  catch (SQLException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
     }
     
     public static void loadComplaints(DefaultTableModel model, String query) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing data and set column names
            model.setRowCount(0);
            model.setColumnCount(0);
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            // Populate rows
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error loading table data: " + e.getMessage());
        }
     }
    
     public static boolean addComplaint(String category, String subject, String description, Date sqlDate, String image) {
            String sql = "INSERT INTO complaints (user,type, subject, description, complaint_date ,image) VALUES (?,?, ?, ?, ?, ?)";
            
            try{Connection connection =getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql); {
            pstmt.setString(1, currentUser.getEmail());
            pstmt.setString(2, category);
            pstmt.setString(3, subject);
            pstmt.setString(4, description);
            pstmt.setDate(5, sqlDate);
            pstmt.setString(6, image); 

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            }// Return true if the query was successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected static class Admin {
    protected int id;
    protected String email;
    protected String name;

    // Constructor
    public Admin(int id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    }

    protected static class User {
    protected String branch;
    protected String email;
    protected String name;

    // Constructor
    public User(String email, String name,String Branch) {
        this.email = email;
        this.name = name;
        this.branch= Branch;
    }

    // Getters and Setters

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
     public String getBranch() {
        return branch;
    }
    }
}
    
