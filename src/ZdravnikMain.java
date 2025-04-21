package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ZdravnikMain extends JFrame {

    public ZdravnikMain(String ime, String sifra) {
        setTitle("Zdravnik Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2)); // Vertical split: left - pregledi, right - tajnistva

        // Placeholder for "pregledi"
        JPanel leftPanel = new JPanel();
        leftPanel.add(new JLabel("Pregledi (coming soon...)"));
        add(leftPanel);

        // Right Panel for Tajnistva
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Pozdravljen/a: " + ime, SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(label, BorderLayout.CENTER);

        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> new ZdravnikSettings(ime, sifra).setVisible(true));
        topPanel.add(settingsButton, BorderLayout.EAST);

        rightPanel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Ime", "Email", "Telefon", "Glavni tajnik/ca", "Naslov", "Kraj"};
        List<Object[]> dataList = Connection.getTajnistva();
        Object[][] data = dataList.toArray(new Object[0][]);

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false; // disable cell editing
            }
        };

        JTable table = new JTable(model);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0); // hide ID column

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        add(rightPanel);
        setVisible(true);
    }
}