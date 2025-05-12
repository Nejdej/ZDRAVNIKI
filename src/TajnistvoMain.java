package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TajnistvoMain extends JFrame implements TajnistvoUpdateListener {

    private static int id;
    private JLabel label1;
    private JLabel label3;
    private static DefaultTableModel model;
    private static JTable table;

    public TajnistvoMain(int id, String ime, String glavniTajnikCa) {
        this.id = id;

        setTitle("Portal " + ime);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        label1 = new JLabel("Pozdravljeni, " + ime + "!", SwingConstants.CENTER);
        label3 = new JLabel("Glavni tajnik/ca: " + glavniTajnikCa, SwingConstants.CENTER);
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);
        label3.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(label1);
        panel.add(label3);
        panel.add(Box.createVerticalStrut(10));

        // Table setup
        String[] columnNames = {"ID", "Ime oddelka", "Opis", "Tajništvo"};
        Object[][] data = Connection.getOddelkiForTajnistvo(id);

        model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        // Hide ID and Tajnistvo columns
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(3).setMinWidth(0);
        table.getColumnModel().getColumn(3).setMaxWidth(0);
        table.getColumnModel().getColumn(3).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        panel.add(scrollPane);
        panel.add(Box.createVerticalGlue());

        JButton dodajButton = new JButton("➕ Dodaj oddelek");
        dodajButton.addActionListener(e -> new DodajOddelekWindow(id, TajnistvoMain.this));

        panel.add(Box.createVerticalStrut(10));
        panel.add(dodajButton);

        add(panel);
        setVisible(true);

        // Double-click logic
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int oddelekId = (int) table.getValueAt(row, 0);
                        new DelavciMain(oddelekId, id);
                    }
                }
            }
        });

        // Top panel with Logout, Settings, and Refresh button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton krajiButton = new JButton("Kraji");
        krajiButton.addActionListener(e -> new KrajiWindow(id));


        panel.add(krajiButton);
        JButton logoutButton = new JButton("⏎ Nazaj / Odjava");
        logoutButton.addActionListener(e -> {
            dispose(); // Close current window
            new StartScreen().createAndShowGUI();
        });

        JButton settingsButton = new JButton("⚙ Nastavitve");
        settingsButton.addActionListener(e -> new TajnistvoSettings(id, TajnistvoMain.this, TajnistvoMain.this));

        JButton refreshButton = new JButton("Osveži");
        refreshButton.addActionListener(e -> refreshTable());

        topPanel.add(logoutButton);
        topPanel.add(settingsButton);
        topPanel.add(refreshButton);
        topPanel.add(krajiButton);

        getContentPane().add(topPanel, BorderLayout.NORTH);
    }

    @Override
    public void onTajnistvoUpdated() {
        Object[] data = Connection.getTajnistvoById(id);
        if (data != null) {
            String ime = (String) data[1];
            String glavniTajnikCa = (String) data[4];

            label1.setText("Pozdravljeni, " + ime + "!");
            label3.setText("Glavni tajnik/ca: " + glavniTajnikCa);
        }
    }

    // Method to refresh the table data
    public static void refreshTable() {
        Object[][] data = Connection.getOddelkiForTajnistvo(id);
        String[] columnNames = {"ID", "Ime oddelka", "Opis", "Tajništvo"};

        model.setDataVector(data, columnNames);

        // Re-hide ID and Tajnistvo columns on the actual table
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(3).setMinWidth(0);
        table.getColumnModel().getColumn(3).setMaxWidth(0);
        table.getColumnModel().getColumn(3).setWidth(0);
    }
}