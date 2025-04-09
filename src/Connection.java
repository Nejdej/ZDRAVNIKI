package src;

import java.sql.*;

public class Connection {

    public static java.sql.Connection connectToDatabase() {
        String url = EnvLoader.getEnv("DB_HOST");
        String username = EnvLoader.getEnv("DB_USER");
        String password = EnvLoader.getEnv("DB_PASSWORD");

        java.sql.Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database successfully!");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
        return connection;
    }

    public static boolean checkZdravnikSifra(String sifra) {
        boolean result = false;
        String query = "SELECT checkZdravnikSifra(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sifra);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result = rs.getBoolean(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking zdravnik šifra: " + e.getMessage());
        }

        return result;
    }

    public static boolean checkTajnistvoCredentials(String email, String password) {
        boolean result = false;
        String query = "SELECT checkTajnistvoCredentials(?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result = rs.getBoolean(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking tajnistvo credentials: " + e.getMessage());
        }

        return result;
    }
}