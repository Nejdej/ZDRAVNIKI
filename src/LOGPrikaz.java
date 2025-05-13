package src;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import static src.Connection.getAllLogEntries;

public class LOGPrikaz extends JFrame {

    public LOGPrikaz() {
        setTitle("Prikaz Logov");
        setSize(800, 500);
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Fetch log entries from the database
        List<Object[]> logEntries = getAllLogEntries();

        // Column names for the table
        String[] columns = {"Ime", "Priimek", "EMŠO", "Telefon", "Oddelek ID", "Datum spremembe", "Tip spremembe"};

        // Table data: converting List<Object[]> into Object[][]
        Object[][] data = new Object[logEntries.size()][7];
        for (int i = 0; i < logEntries.size(); i++) {
            data[i] = logEntries.get(i);
        }

        // Create the table
        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);

        // Put the table inside a JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Add the scroll pane to the frame
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true); // Make the window visible
    }
}