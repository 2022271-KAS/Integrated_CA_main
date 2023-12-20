package integrated_ca_main;

import static integrated_ca_main.DataBase.*;
import ioutils.IOUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    
        //Method to modify the admin's info.
    public void modifyAdminInfo() {
        try (
            //It connects to the database.
            Connection conn = DriverManager.getConnection(DataBase.DB_BASE_URL, DataBase.USER, DataBase.PASSWORD);  
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM userData WHERE id = ?");) {
            
            //It uses the "USE" statement to select the database
            stmt.execute("USE " + DataBase.DB_NAME);
            //It sets the admin ID to retrieve information
            stmt.setInt(1, 1);

            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    //It retrieves admin information from the userData table.
                    int adminId = rs.getInt("id");
                    String adminFirstName = rs.getString("first_name");
                    String adminLastName = rs.getString("last_name");
                    int adminAge = rs.getInt("age");
                    String adminMaritalStatus = rs.getString("marital_status");
                    double adminWeeklyIncome = rs.getDouble("weekly_income");
                    System.out.println();
                    //It displays the current admin information.
                    System.out.println("Current Admin Information:");
                    System.out.println("First Name: " + adminFirstName);
                    System.out.println("Last Name: " + adminLastName);
                    System.out.println("Age: " + adminAge);
                    System.out.println("Marital Status: " + adminMaritalStatus);
                    System.out.println("Weekly Income: " + adminWeeklyIncome);
                    System.out.println();

                    //Then it lers the admin modify the new first and last name.
                    IOUtils myInput = new IOUtils();
                    String newFN = myInput.getUserText("Enter new first name:");
                    String newLN = myInput.getUserText("Enter new last name:");
                    //We kept the age, marital status and income always set as default. 
                    int newAge = 0;
                    String newMaritalStatus = "x";
                    double newWeeklyIncome = 0.0;

                    //It updates the admin's information in the userData table using a method from the dbWriter class. 
                    writer.updateAdminInfo(adminId, newFN, newLN, newAge, newMaritalStatus, newWeeklyIncome);
                    System.out.println();
                    //Then it displays the updated admin information
                    System.out.println("Admin Information Updated Successfully:");
                    System.out.println("First Name: " + newFN);
                    System.out.println("Last Name: " + newLN);
                    System.out.println("Age: " + newAge);
                    System.out.println("Marital Status: " + newMaritalStatus);
                    System.out.println("Weekly Income: " + newWeeklyIncome);
                    System.out.println();
                } else {
                    //Error message.
                    System.out.println("Admin not found in the database.");
                }
            }
        } catch (SQLException e) {
            System.out.println("ModifyAdminInfo() - Error updating admin's info.");
            e.printStackTrace();
        }
    }
}
