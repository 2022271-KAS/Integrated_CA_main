package integrated_ca_main;

import static integrated_ca_main.DataBase.*;
import ioutils.IOUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


//This is the Admin class that will handle all admin's activities related. 
public class Admin {
    
    //It declares a constant reference to the instance of the dbWriter class so I can use it in this class.
    //It needs to be initialized.
    private final DbWriter writer = new DbWriter();

    //IOUtils to let the admin input.
    IOUtils myInput = new IOUtils();
    //Method for the adminMenu.
    
    
    
    
    
    
    
    
    //Method that allows the admin to create new users and add them to the database.
    public static boolean createUser() throws SQLException {

        //IOUtils to collect admin's input.
        IOUtils myInput = new IOUtils();

        //Variables to store the user's credentials, userName and password
        String userName = myInput.getUserText("Enter username:");
        String userPassword = myInput.getUserText2("Enter password:");

        //It creates a new User object that holds the user's input.
        User newUser = new User();

        //Try catch to write the user's input into the database.
        try (
            //It connects to the database.    
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);  Statement stmt = conn.createStatement();) {
            // How the info is going to be written into the userData table.
            String userDataSql = String.format("INSERT INTO %s (id, first_name, last_name, age, marital_status, weekly_income) VALUES "
                + "(%d,'%s', '%s', %d, '%s', %.2f);",
                TABLE_NAME, 
                newUser.getId(), 
                newUser.getFn(), 
                newUser.getLn(), 
                newUser.getAge(), 
                newUser.getMs(), 
                newUser.getWi());

        //It executes the command to insert into the userData table.
        stmt.execute(userDataSql);

            //Using a prepared statement for the user_credentials table.
            String credentialsSql = "INSERT INTO user_credentials (username, password) VALUES (?, ?)";
            //Part dealing with the username.
            try ( PreparedStatement stmt2 = conn.prepareStatement(credentialsSql)) {

                //Setting values for placeholders, this will save the username and the password.
                stmt2.setString(1, userName);
                stmt2.setString(2, userPassword);

                //Executing the prepared statement.
                stmt2.executeUpdate();

                return true;
                //Catch errors.
            } catch (Exception e) {
                System.out.println("CreateUser() - Error creating new user.");
                e.printStackTrace();
                return false;
            }
        }
    }
    
    
}
