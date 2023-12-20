package integrated_ca_main;

//This class is used for writing into the database, it implements the DataBase interface.

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DbWriter implements DataBase {
    
        //Method that allows to create new users and add them to the database.
    public static boolean createUser() throws SQLException {

        //IOUtils to collect user's input.
        IOUtils myInput = new IOUtils();

        //Variables to store the user's credentials, userName and password
        String userName = myInput.getUserText2("Enter username:");
        String userPassword = myInput.getUserText2("Enter password:");

        
        //It creates a new User object that holds the user's input.
        User newUser = new User();
        

        //Try catch to write the user's input into the database.
        try (
            //It connects to the database.   
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);  
            Statement stmt = conn.createStatement();) {
            //It defines how the info is going to be written into the table.
            //SQL is very case sensitve, so any mistakes here will cause errors. 
            String sql = String.format("INSERT INTO %s (id, first_name, last_name, age, marital_status, weekly_income) VALUES "
                + "(%d,'%s', '%s', %d, '%s', %.2f);",
                TABLE_NAME,
                //We use getters to access the user's information that was just created.
                newUser.getCurrentID(), 
                newUser.getFn(), 
                newUser.getLn(), 
                newUser.getAge(), 
                newUser.getMs(), 
                newUser.getWi());

            //What executes the commands written here to the database.
            stmt.execute(sql);

            //It uses a prepared statement for the user_credentials table to store the username and password into the user_credentials table.
            String credentialsSql = "INSERT INTO user_credentials (id, username, password) VALUES (?, ?, ?)";
            try ( PreparedStatement stmt2 = conn.prepareStatement(credentialsSql)) {

                //It saves the id, username and the password.
                stmt2.setInt(1, newUser.getCurrentID());
                stmt2.setString(2, userName);
                stmt2.setString(3, userPassword);

                //It executes and updates.
                stmt2.executeUpdate();

                return true;
            //Catch errors.
            } catch (Exception e) {
                System.out.println("CreateUser() - Failed to create new user.");
                e.printStackTrace();
                return false;
            }
        }
    }
    
}
