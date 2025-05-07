package src;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static Object[][] getOddelkiForTajnistvo(int tajnistvoId) {
        String query = "SELECT * FROM prikaziOddelkeVTajnistvu(?)";
        Object[][] data = new Object[0][];
        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                     query,
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY)) {

            stmt.setInt(1, tajnistvoId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.last();
                int rowCount = rs.getRow();
                rs.beforeFirst();

                data = new Object[rowCount][4];
                int i = 0;
                while (rs.next()) {
                    data[i][0] = rs.getInt("iid");
                    data[i][1] = rs.getString("iime");
                    data[i][2] = rs.getString("oopis");
                    data[i][3] = rs.getString("ttajnistvo");
                    i++;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving oddelki: " + e.getMessage());
        }
        return data;
    }

    public static List<Object[]> getDelavciForOddelek(int oid) {
        List<Object[]> workers = new ArrayList<>();
        String query = "SELECT * FROM prikaziDelavceVoddelku(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, oid);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] worker = new Object[]{
                            rs.getInt("iid"),
                            rs.getString("iime"),
                            rs.getString("ppriimek"),
                            rs.getString("eemso"),
                            rs.getString("ttelefon"),
                            rs.getString("sslikica"), // you can convert this to ImageIcon later
                            rs.getString("ooddelek"),
                            rs.getString("ttajnistvo"),
                            rs.getString("kkraj")
                    };
                    workers.add(worker);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving delavci: " + e.getMessage());
        }

        return workers;
    }

    public static List<Object[]> getTajnistva() {
        List<Object[]> tajnistva = new ArrayList<>();
        String query = "SELECT * FROM prikaziTajnistva()";

        try (java.sql.Connection conn = connectToDatabase();
        PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("iid");
                row[1] = rs.getString("iime");
                row[2] = rs.getString("eemail");
                row[3] = rs.getString("ttelefon");
                row[4] = rs.getString("gglavnia_tajnikca");
                row[5] = rs.getString("nnaslov");
                row[6] = rs.getString("kkraj");
                tajnistva.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tajnistva;
    }

    public static void updateZdravnik(String csifra, String nsifra, String cnaziv, String nnaziv) {
        String query = "SELECT updajtajZdravnika(?, ?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, csifra);
            stmt.setString(2, nsifra);
            stmt.setString(3, cnaziv);
            stmt.setString(4, nnaziv);

            stmt.execute();
            System.out.println("Zdravnik successfully updated.");

        } catch (SQLException e) {
            System.out.println("Error updating zdravnik: " + e.getMessage());
        }
    }

    public static void deleteZdravnik(String csifra) {
        String query = "SELECT deletajZdravnika(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, csifra);
            stmt.execute();
            System.out.println("Zdravnik successfully deleted.");

        } catch (SQLException e) {
            System.out.println("Error deleting zdravnik: " + e.getMessage());
        }
    }
    public static Object[] getTajnistvoById(int tid) {
        Object[] tajnistvo = null;
        String query = "SELECT * FROM prikaziTajnistvo(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tajnistvo = new Object[8];
                tajnistvo[0] = rs.getInt("iid");
                tajnistvo[1] = rs.getString("iime");
                tajnistvo[2] = rs.getString("eemail");
                tajnistvo[3] = rs.getString("ttelefon");
                tajnistvo[4] = rs.getString("gglavnia_tajnikca");
                tajnistvo[5] = rs.getString("nnaslov");
                tajnistvo[6] = rs.getString("kkraj");
                tajnistvo[7] = rs.getString("ppass");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving tajnistvo by ID: " + e.getMessage());
        }

        return tajnistvo;
    }
    public static void updateTajnistvo(int tid, String ime, String email, String telefon, String glavniTajnik, String naslov, String posta) {
        String query = "SELECT updajtajTajnistvo(?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tid);
            stmt.setString(2, ime);
            stmt.setString(3, email);
            stmt.setString(4, telefon);
            stmt.setString(5, glavniTajnik);
            stmt.setString(6, naslov);
            stmt.setString(7, posta);

            stmt.execute();
            System.out.println("Tajnistvo updated successfully.");

        } catch (SQLException e) {
            System.out.println("Error updating tajnistvo: " + e.getMessage());
        }

    }

    public static List<Object[]> getAllKraji() {
        List<Object[]> kraji = new ArrayList<>();
        String query = "SELECT * FROM prikaziKraje()";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getInt("iid");     // ID
                row[1] = rs.getString("iime"); // Name
                row[2] = rs.getString("pposta"); // Posta
                kraji.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kraji;
    }

    public static Object[] getKrajByPosta(String posta) {
        Object[] kraj = null;
        String query = "SELECT * FROM prikaziKraj(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, posta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                kraj = new Object[]{
                        rs.getInt("iid"),
                        rs.getString("iime"),
                        rs.getString("pposta")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kraj;
    }

    public static void insertajPregled(String leto, Timestamp datum, String opombe, String emso) {
        String query = "SELECT insertajPregled(?, ?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, leto);
            stmt.setTimestamp(2, datum);
            stmt.setString(3, opombe);
            stmt.setString(4, emso);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Object[]> getPreglediForDelavec(String emso) {
        List<Object[]> pregledi = new ArrayList<>();
        String query = "SELECT * FROM prikaziPregledDelavca(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, emso);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("iid");
                row[1] = rs.getTimestamp("ddatum");
                row[2] = rs.getString("oopombe");
                row[3] = rs.getString("eemso");
                pregledi.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pregledi;
    }

    public static void deletajDelavca(String emso) {
        String query = "SELECT deletajDelavca(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, emso);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri brisanju delavca!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void updajtajDelavca(String currentEmso, String newEmso, String ime, String priimek,
                                       String telefon, String slikica, String oddelekIme) {

        String query = "SELECT updajtajDelavca(?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, currentEmso);
            stmt.setString(2, newEmso);
            stmt.setString(3, ime);
            stmt.setString(4, priimek);
            stmt.setString(5, telefon);
            stmt.setString(6, slikica);
            stmt.setString(7, oddelekIme);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri posodabljanju delavca!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static List<Object[]> prikaziOddelkeVTajnistvu(int tajnistvoId) {
        List<Object[]> oddelki = new ArrayList<>();
        String query = "SELECT * FROM prikaziOddelkeVTajnistvu(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tajnistvoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getInt("iid");
                row[1] = rs.getString("iime");
                row[2] = rs.getString("oopis");
                row[3] = rs.getString("ttajnistvo");
                oddelki.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri pridobivanju oddelkov!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }
        return oddelki;
    }
    public static void insertajDelavca(String ime, String priimek, String emso, String telefon, String slikica, String oddelekIme) {
        String query = "SELECT insertajDelavca(?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ime);
            stmt.setString(2, priimek);
            stmt.setString(3, emso);
            stmt.setString(4, telefon);
            stmt.setString(5, slikica);
            stmt.setString(6, oddelekIme);

            stmt.execute(); // No ResultSet needed since it returns void

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri vstavljanju delavca!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updajtajOddelek(int oid, String oime, String oopis, String tiime) {
        try {
            java.sql.Connection conn = connectToDatabase();
            PreparedStatement stmt = conn.prepareStatement("SELECT updajtajOddelek(?, ?, ?, ?)");
            stmt.setInt(1, oid);
            stmt.setString(2, oime);
            stmt.setString(3, oopis);
            stmt.setString(4, tiime);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletajOddelek(int oid) {
        try {
            java.sql.Connection conn = connectToDatabase();
            PreparedStatement stmt = conn.prepareStatement("SELECT deletajOddelek(?)");
            stmt.setInt(1, oid);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<String> prikaziOddelek(int oid) {
        List<String> data = new ArrayList<>();
        try {
            java.sql.Connection conn = connectToDatabase();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM prikaziOddelek(?)");
            stmt.setInt(1, oid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                data.add(String.valueOf(rs.getInt("iid")));
                data.add(rs.getString("iime"));
                data.add(rs.getString("oopis"));
                data.add(rs.getString("ttajnistvo"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}