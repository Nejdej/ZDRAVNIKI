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

    public static String checkZdravnikSifra(String sifra) {
        String result = null;
        String query = "SELECT * FROM checkZdravnikSifra(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sifra);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String ime = rs.getString("iime");
                    if (!"FALSE".equals(ime)) {
                        result = ime;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking zdravnik šifra: " + e.getMessage());
        }

        return result;
    }

    public static Object[] checkTajnistvoCredentials(String email, String password) {
        Object[] result = null;
        String query = "SELECT * FROM checkTajnistvoCredentials(?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("iid");
                    String ime = rs.getString("iime");
                    String glavniTajnikCa = rs.getString("gglavnia_tajnikca");

                    if (id != -1) {
                        result = new Object[]{id, ime, glavniTajnikCa};
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking tajnistvo credentials: " + e.getMessage());
        }

        return result;
    }
}