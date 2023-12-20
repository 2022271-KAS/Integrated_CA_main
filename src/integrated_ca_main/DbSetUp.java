package integrated_ca_main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


//This class is used for setting up the database and creating the tables. It implements the DataBase.
public class DbSetUp {
    //Main method to set up the database. Throwed the necessary exceptions.
    public static boolean setupDB() {
    
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
        
        return true; 
    }
    
}
