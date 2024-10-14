import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class PreparedStatementTest {

    private static final String URL = "jdbc:derby://localhost:1527/EmployeeDB";
    private static final String USERNAME = "tiger";
    private static final String PASSWORD = "scott";

    public static void main(String[] args) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
             Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {

            // Prepare the SQL statement
            String query = "SELECT * FROM Employee WHERE Salary > ?";
            try (PreparedStatement pStmt = con.prepareStatement(query)) {
                runSalarySearch(in, pStmt);
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void runSalarySearch(BufferedReader in, PreparedStatement pStmt) throws IOException {
        String input;
        while (true) {
            System.out.print("Enter salary to search for or Q to quit: ");
            input = in.readLine();
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Exiting...");
                break;
            }

            try {
                double searchValue = Double.parseDouble(input);
                pStmt.setDouble(1, searchValue);
                try (ResultSet rs = pStmt.executeQuery()) {
                    if (!rs.isBeforeFirst()) { // Check if no records were found
                        System.out.println("No employees found with salary greater than " + searchValue);
                    } else {
                        displayResults(rs);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
            }
        }
    }

    private static void displayResults(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int empID = rs.getInt("ID");
            String first = rs.getString("FIRSTNAME");
            String last = rs.getString("LASTNAME");
            Date birthDate = rs.getDate("BIRTHDATE");
            float salary = rs.getFloat("SALARY");
            System.out.format("Employee ID:   %d%n"
                            + "Employee Name: %s %s%n"
                            + "Birth Date:    %s%n"
                            + "Salary:        %.2f%n%n",
                    empID, first, last, birthDate, salary);
        }
    }
}