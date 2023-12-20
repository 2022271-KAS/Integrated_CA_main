package integrated_ca_main;

import static integrated_ca_main.DataBase.DB_BASE_URL;
import static integrated_ca_main.DataBase.DB_NAME;
import static integrated_ca_main.DataBase.PASSWORD;
import static integrated_ca_main.DataBase.TABLE_NAME;
import static integrated_ca_main.DataBase.USER;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


//This class is used for setting up the database and creating the tables. It implements the DataBase.
public class DbSetUp {
    //Main method to set up the database. Throwed the necessary exceptions.
    public static boolean setupDB() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    
        //It establishes connection with the database using the jdbc driver.
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        
        //Connector that will link the code to the database using its URL, user, and password to validate the access.
        //The information used is defined in the DataBase interface.
        try ( Connection conn = DriverManager.getConnection(DB_BASE_URL, USER, PASSWORD); 
              //Variable to store the commands we're going to give to the database.
              Statement stmt = conn.createStatement();) {

            //Command in SQL to create the database, using the name we defined in the Database interface.
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME + ";");
            //Command to access and use the database.
            stmt.execute("USE " + DB_NAME + ";");

            //Parameters that have to be followed when inserting data into the database.
            //In this case, the command creates a table, if it doesn't already exists, and creates columns for first, last names, age, marital status and also weeklly income.
            //Depending on the input we're going to take from users we have to change this part.
            //The table's name is defined in the DataBase Interface. "UserData".
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                    + "(" + "id INT,"
                    + "first_name VARCHAR(255),"
                    + "last_name VARCHAR(255),"
                    + "age INT,"
                    + "marital_status VARCHAR(255),"
                    + "weekly_income DOUBLE"
                    + ");";

            //It executes the commands in SQL into the database.
            stmt.execute(sql);

            //It creates the user_credentials table to store ids, usernames, and passwords (login data).
            String userCredentials = "CREATE TABLE IF NOT EXISTS user_credentials ("
                    + "id INT,"
                    + "username VARCHAR(255),"
                    + "password VARCHAR(255) NOT NULL"
                    + ");";
            //It executes the given commands in SQL into the database.
            stmt.execute(userCredentials);            
            //Variables to hold the pre-defined ADM's credentials, as requested in the CA descriptor.
            String adminUsername = "CCT";
            String adminPassword = "Dublin";
            
            //It checks if the admin user already exists in user_credentials.
            String userExistsQuery = "SELECT id FROM user_credentials WHERE username = ?";
        try (PreparedStatement userExistsStmt = conn.prepareStatement(userExistsQuery)) {
            //It goes through the user_credentials table looking for the adminUsername.
            userExistsStmt.setString(1, adminUsername);
            //It returns the result of the query.
            ResultSet userExistsResult = userExistsStmt.executeQuery();
            //If the admin registred in the database the message is displayed.
            if (userExistsResult.next()) {
                //The admin exists in the database.
                //I was using this as a debug message but will comment it just to make the running code clear. 
                //System.out.println("Admin mode available.");
            //If the admin username is not found in the database then...
            }else {

            //Insert admin credentials into user_credentials
            String insertAdminCredentials = "INSERT INTO user_credentials (id, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement adminStmt = conn.prepareStatement(insertAdminCredentials)) {
                //The admin's ID will always be 1. 
                adminStmt.setInt(1, 1);
                //It sets the admin's username. 
                adminStmt.setString(2, adminUsername);
                //It sets the admin's password.
                adminStmt.setString(3, adminPassword);
                //It executes the query and updates the database.
                adminStmt.executeUpdate();
                //I was using this message as a debug as well but will comment to make the code clear.
                //System.out.println("Admin credentials inserted into user_credentials successfully.");
            }catch (SQLException e) {
                e.printStackTrace();
            }

            //It inserts the admin information into userData
            String insertAdminInfo = "INSERT INTO userData (id, first_name, last_name, age, marital_status, weekly_income) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement adminInfoStmt = conn.prepareStatement(insertAdminInfo)) {
                //These are default values we've decided to set. 
                //Variables to store the values.
                int admID = 1;
                String defaultFn = "Master";
                String defaultLn = "Admin";
                int defaultAge = 0;
                String defaultMaritalStatus = "x";
                double defaultWeeklyIncome = 0.0;
                //Using the defined variables to insert their values into the database.
                adminInfoStmt.setInt(1, admID);
                adminInfoStmt.setString(2, defaultFn);
                adminInfoStmt.setString(3, defaultLn);
                adminInfoStmt.setInt(4, defaultAge);
                adminInfoStmt.setString(5, defaultMaritalStatus);
                adminInfoStmt.setDouble(6, defaultWeeklyIncome);
                adminInfoStmt.executeUpdate();
                //Success. 
                System.out.println("Admin information inserted into userData successfully.");
            }catch (SQLException e) {
                e.printStackTrace();
            }        
        }//Catch
    } catch (SQLException e) {
        e.printStackTrace();
    }

        }catch(Exception e){
            //Error message.
            System.out.println("dbSetUp() - Error creating tables.");
            return false;
        }
        
        return true; 
    }
    
}
