import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/moneymanager";
            String username = "root"; // change if needed
            String password = "";     // add your password if set

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connected to MySQL!");
            return conn;

        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            e.printStackTrace();
            return null;
        }
    }
}
