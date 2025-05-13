package src;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
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
        String encrypted = encryptPassword(sifra);
        String query = "SELECT * FROM checkZdravnikSifra(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, encrypted);

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
        String encrypted = encryptPassword(password);
        String query = "SELECT * FROM checkTajnistvoCredentials(?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, encrypted);

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
                            (Object) rs.getInt("iid"),
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
                row[0] = (Object) rs.getInt("iid");
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
        String encryptedCurrent = encryptPassword(csifra);
        String encryptedNew = encryptPassword(nsifra);
        String query = "SELECT updajtajZdravnika(?, ?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, encryptedCurrent);
            stmt.setString(2, encryptedNew);
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
                tajnistvo[0] = (Object) rs.getInt("iid");
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
    public static void updateTajnistvo(int tid, String ime, String email, String telefon, String glavniTajnik, String naslov, String posta, String geslo) {
        String encrypted = encryptPassword(geslo);
        String query = "SELECT updajtajTajnistvo(?, ?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tid);
            stmt.setString(2, ime);
            stmt.setString(3, email);
            stmt.setString(4, telefon);
            stmt.setString(5, glavniTajnik);
            stmt.setString(6, naslov);
            stmt.setString(7, posta);
            stmt.setString(8, encrypted);

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
                row[0] = (Object) rs.getInt("iid");     // ID
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
                        (Object) rs.getInt("iid"),
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
                row[0] = (Object) rs.getInt("iid");
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

    public static void updajtajOddelek(int oid, String oime, String oopis, String tiime) {
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

    public static void deletajOddelek(int oid) {
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
    public static List<String> prikaziOddelek(int oid) {
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
    public static void insertajOddelek(String oime, String oopis, String tiime) {
        String query = "SELECT insertajOddelek(?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, oime);
            stmt.setString(2, oopis);
            stmt.setString(3, tiime);

            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri vstavljanju oddelka!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void deleteTajnistvo(int id) {
        String query = "SELECT deletajTajnistvo(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri brisanju tajništva!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static List<Object[]> getAllPregledi() {
        List<Object[]> mergedList = new ArrayList<>();

        String normalQuery = "SELECT * FROM prikaziPreglede()";
        String deletedQuery = "SELECT * FROM prikaziPregledeDeleted()";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement normalStmt = conn.prepareStatement(normalQuery);
             PreparedStatement deletedStmt = conn.prepareStatement(deletedQuery)) {

            ResultSet rsNormal = normalStmt.executeQuery();
            while (rsNormal.next()) {
                Object[] row = {
                        rsNormal.getString("iid"),
                        rsNormal.getTimestamp("ddatum"),
                        rsNormal.getString("oopombe"),
                        rsNormal.getString("eemso")
                };
                mergedList.add(row);
            }

            ResultSet rsDeleted = deletedStmt.executeQuery();
            while (rsDeleted.next()) {
                Object[] row = {
                        rsDeleted.getString("iid"),
                        rsDeleted.getTimestamp("ddatum"),
                        rsDeleted.getString("oopombe"),
                        rsDeleted.getString("eemso")
                };
                mergedList.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri pridobivanju pregledov!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }

        return mergedList;
    }

    public static List<Object[]> getPreglediForTajnistvo(int tid) {
        List<Object[]> preglediList = new ArrayList<>();
        String query = "SELECT * FROM prikaziPregledTajnistva(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getString("iid"),
                            rs.getTimestamp("ddatum"),
                            rs.getString("oopombe"),
                            rs.getString("eemso")
                    };
                    preglediList.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri pridobivanju pregledov!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }

        return preglediList;
    }

    public static List<Object[]> getPreglediForOddelek(int oddelekId) {
        List<Object[]> preglediList = new ArrayList<>();
        String query = "SELECT * FROM prikaziPregledOddelka(?)"; // Calling the function

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the ID parameter for the query
            stmt.setInt(1, oddelekId);

            // Execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Extract the data from the result set and add it to the list
                    Object[] pregledi = new Object[]{
                            rs.getString("iid"),
                            rs.getTimestamp("ddatum"),
                            rs.getString("oopombe"),
                            rs.getString("eemso")
                    };
                    preglediList.add(pregledi);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving pregledi for oddelek: " + e.getMessage());
        }

        return preglediList;
    }

    public static void updajtajPregled(String oldId, String newId, Timestamp datum, String opombe, String emso) {
        String query = "SELECT updajtajPregled(?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, oldId);
            stmt.setString(2, newId);
            stmt.setTimestamp(3, datum);
            stmt.setString(4, opombe);
            stmt.setString(5, emso);

            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri posodabljanju pregleda!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void deletajPregled(String id) {
        String query = "SELECT deletajPregled(?::varchar)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);  // id as varchar

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri brisanju pregleda!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String insertajZdravnika(String patronsifra, String ssifra, String nnaziv) {
        String result = null;
        String encryptedPatron = encryptPassword(patronsifra);
        String encryptedSelf = encryptPassword(ssifra);
        String query = "SELECT insertajZdravnika(?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, encryptedPatron);
            stmt.setString(2, encryptedSelf);
            stmt.setString(3, nnaziv);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error inserting zdravnik: " + e.getMessage());
        }

        return result;
    }

    public static boolean insertajTajnistvo(String ime, String email, String telefon,
                                            String glavniTajnikCa, String naslov, String posta, String pass) {
        String encrypted = encryptPassword(pass);
        String query = "SELECT insertajTajnistvo(?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ime);
            stmt.setString(2, email);
            stmt.setString(3, telefon);
            stmt.setString(4, glavniTajnikCa);
            stmt.setString(5, naslov);
            stmt.setString(6, posta);
            stmt.setString(7, encrypted);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error inserting tajnistvo: " + e.getMessage());
        }
        return false;
    }

    public static boolean pokreniPregledeZaOddelek(int oddelekId, String idLetosnjegaLeta, Timestamp datum, String opombe) {
        String call = "{ call oddelekcelpregled(?, ?, ?, ?) }";

        try (java.sql.Connection conn = connectToDatabase();
             CallableStatement stmt = conn.prepareCall(call)) {

            stmt.setInt(1, oddelekId);
            stmt.setString(2, idLetosnjegaLeta);
            stmt.setTimestamp(3, datum);
            stmt.setString(4, opombe);

            stmt.execute(); // It returns VOID, so we just execute
            return true;

        } catch (SQLException e) {
            System.out.println("Napaka pri izvajanju oddelekcelpregled: " + e.getMessage());
            return false;
        }
    }

    public static void insertKraj(String kime, String kposta) {
        String query = "SELECT insertajKraj(?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, kime);
            stmt.setString(2, kposta);

            // Execute the insert
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateKraj(String ckposta, String nkrajime, String nkrajposta) {
        String query = "SELECT updajtajKraj(?, ?, ?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ckposta);
            stmt.setString(2, nkrajime);
            stmt.setString(3, nkrajposta);

            // Execute the update
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteKraj(String kposta) {
        String query = "SELECT deletajKraj(?)";

        try (java.sql.Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, kposta);

            // Execute the delete
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String SECRET_KEY = EnvLoader.getEnv("SECRET_KEY"); // Loaded from .env
    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;

    public static String encryptPassword(String password) {
        try {
            if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
                throw new IllegalStateException("SECRET_KEY not set in .env file.");
            }

            byte[] saltBytes = SECRET_KEY.getBytes();
            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }
}