package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TajnistvoMain extends JFrame implements TajnistvoUpdateListener {

    private int id;
    private JLabel label1;
    private JLabel label3;

    public TajnistvoMain(int id, String ime, String glavniTajnikCa) {
        this.id = id;

        setTitle("Tajništvo Portal");
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

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

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

        // Top panel with Logout and Settings
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton logoutButton = new JButton("⏎ Nazaj / Odjava");
        logoutButton.addActionListener(e -> {
            dispose(); // Close current window
            new startScreen().createAndShowGUI();
        });

        JButton settingsButton = new JButton("⚙ Nastavitve");
        settingsButton.addActionListener(e -> new TajnistvoSettings(id, TajnistvoMain.this));

        topPanel.add(logoutButton);
        topPanel.add(settingsButton);

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
}