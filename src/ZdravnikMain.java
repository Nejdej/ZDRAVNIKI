package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class ZdravnikMain extends JFrame {

    private JTable preglediTable;
    private JPanel leftPanel;
    private JTable oddelkiTable;
    private JTable delavciTable;

    private DefaultTableModel preglediModel;
    private DefaultTableModel oddelkiModel;
    private DefaultTableModel delavciModel;

    private JPanel rightPanel;
    private JTabbedPane tabbedPane;
    private JTable tajnistvaTable;

    public ZdravnikMain(String ime, String sifra) {
        setTitle("Zdravnik Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        // LEFT PANEL: Pregledi
        leftPanel = new JPanel(new BorderLayout());
        add(leftPanel);

        // Buttons panel for filtering pregledi
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton showAllButton = new JButton("Vsi pregledi");
        JButton showUpcomingButton = new JButton("Prihajajoči pregledi");
        JButton showPastButton = new JButton("Pretekli pregledi");

        buttonsPanel.add(showAllButton);
        buttonsPanel.add(showUpcomingButton);
        buttonsPanel.add(showPastButton);

        preglediTable = new JTable();
        JScrollPane preglediScrollPane = new JScrollPane(preglediTable);


        // Shared columns
        String[] columns = {"ID", "Datum", "Opombe", "EMŠO"};

        // Button actions
        showAllButton.addActionListener(e -> {
            List<Object[]> list = Connection.getAllPregledi();
            setPreglediTable(list, columns);
            tabbedPane.setSelectedIndex(0);
        });

        showUpcomingButton.addActionListener(e -> {
            List<Object[]> list = Connection.getAllPregledi();
            List<Object[]> filtered = new ArrayList<>();
            LocalDate today = LocalDate.now();

            for (Object[] pregled : list) {
                try {
                    String datumString = pregled[1].toString();
                    LocalDate datum = LocalDate.parse(datumString.substring(0, 10));
                    if (datum.isAfter(today)) {
                        filtered.add(pregled);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(); // Optional: for debugging any malformed date
                }
            }

            setPreglediTable(filtered, columns);
            tabbedPane.setSelectedIndex(0);
        });

        showPastButton.addActionListener(e -> {
            List<Object[]> list = Connection.getAllPregledi();
            List<Object[]> filtered = new ArrayList<>();
            LocalDate today = LocalDate.now();

            for (Object[] pregled : list) {
                try {
                    String datumString = pregled[1].toString();
                    LocalDate datum = LocalDate.parse(datumString.substring(0, 10));
                    if (datum.isBefore(today)) {
                        filtered.add(pregled);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(); // Optional: for debugging any malformed date
                }
            }

            setPreglediTable(filtered, columns);
            tabbedPane.setSelectedIndex(0);
        });

        leftPanel.add(buttonsPanel, BorderLayout.NORTH);
        leftPanel.add(preglediScrollPane, BorderLayout.CENTER);

        // RIGHT PANEL: Tajnistva, Oddelki, Delavci
        rightPanel = new JPanel(new BorderLayout());
        add(rightPanel);

        // Top panel with greeting + buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Pozdravljen/a: " + ime, SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(label, BorderLayout.CENTER);

        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> {
            ZdravnikSettings settings = new ZdravnikSettings(ime, sifra, (newIme, newSifra) -> {
                dispose(); // Close this ZdravnikMain before opening updated one
                new ZdravnikMain(newIme, newSifra);
            }, this); // 👈 Pass this frame to settings window
            settings.setVisible(true);
        });

        JButton logoutButton = new JButton("⏎ Nazaj / Odjava");
        logoutButton.addActionListener(e -> {
            dispose();
            new StartScreen().createAndShowGUI();
        });

        topPanel.add(logoutButton, BorderLayout.WEST);
        topPanel.add(settingsButton, BorderLayout.EAST);

        rightPanel.add(topPanel, BorderLayout.NORTH);

        // Create bottom panel with Naroči button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton narociButton = new JButton("Naroči pregled");
        narociButton.setEnabled(false);
        bottomPanel.add(narociButton);

// Add bottom panel to rightPanel
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Tabs for Tajnistva / Oddelki / Delavci
        tabbedPane = new JTabbedPane();

        // Tajnistva tab
        String[] tajnistvaColumns = {"ID", "Ime", "Email", "Telefon", "Glavni tajnik/ca", "Naslov", "Kraj"};
        List<Object[]> tajnistvaList = Connection.getTajnistva();
        Object[][] tajnistvaData = tajnistvaList.toArray(new Object[0][]);
        tajnistvaTable = new JTable(new DefaultTableModel(tajnistvaData, tajnistvaColumns) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        tajnistvaTable.getColumnModel().getColumn(0).setMinWidth(0);
        tajnistvaTable.getColumnModel().getColumn(0).setMaxWidth(0);
        tajnistvaTable.getColumnModel().getColumn(0).setWidth(0); // Hide ID

        JScrollPane tajnistvaScroll = new JScrollPane(tajnistvaTable);
        tabbedPane.add("Tajnistva", tajnistvaScroll);

        // Oddelki tab
        oddelkiTable = new JTable();
        JScrollPane oddelkiScroll = new JScrollPane(oddelkiTable);
        tabbedPane.add("Oddelki", oddelkiScroll);

        // Delavci tab
        delavciTable = new JTable();
        JScrollPane delavciScroll = new JScrollPane(delavciTable);
        tabbedPane.add("Delavci", delavciScroll);

        rightPanel.add(tabbedPane, BorderLayout.CENTER);

        // Double click on tajnistvo
        tajnistvaTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tajnistvaTable.getSelectedRow();
                    int tajnistvoId = (int) tajnistvaTable.getValueAt(row, 0);
                    updatePreglediForTajnistvo(tajnistvoId);
                    updateOddelkiForTajnistvo(tajnistvoId);
                    tabbedPane.setSelectedIndex(1); // Switch to Oddelki
                }
                if (e.getClickCount() == 1) {
                    narociButton.setEnabled(false); // Enable the button
                }
            }
        });

        // Double click on oddelek
        oddelkiTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    narociButton.setEnabled(true); // Enable the button
                }

                if (e.getClickCount() == 2) {
                    int row = oddelkiTable.getSelectedRow();
                    int oddelekId = (int) oddelkiTable.getValueAt(row, 0);
                    updatePreglediForOddelek(oddelekId);
                    updateDelavciForOddelek(oddelekId);
                    tabbedPane.setSelectedIndex(2);
                }
            }
        });

        narociButton.addActionListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();

            // If we're on the Delavci tab (assuming index 2)
            if (selectedTab == 2) {
                int selectedRow = delavciTable.getSelectedRow();
                if (selectedRow != -1) {
                    String emso = delavciTable.getValueAt(selectedRow, 3).toString();
                    new NarociPregledWindow(emso); // <-- Opens the NarociPregledWindow
                }
            }

            // If we're on the Oddelki tab (assuming index 1)
            else if (selectedTab == 1) {
                int selectedRow = oddelkiTable.getSelectedRow();
                if (selectedRow != -1) {
                    int oddelekId = (int) oddelkiTable.getValueAt(selectedRow, 0);
                    new OddelekNaroci(oddelekId).setVisible(true);
                    // You probably don’t want to open NarociPregledWindow here unless you also have EMŠO
                }
            }

            // Else do nothing or disable button
        });

        // Double click on delavec
        delavciTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = delavciTable.getSelectedRow();
                    String emso = delavciTable.getValueAt(row, 3).toString(); // 4th column is EMŠO
                    updatePreglediForDelavec(emso);
                }
                if (e.getClickCount() == 1) {
                    narociButton.setEnabled(true); // Enable the button
                }
            }
        });

        preglediTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && preglediTable.getSelectedRow() != -1) {
                    int row = preglediTable.getSelectedRow();
                    String id = (String) preglediTable.getValueAt(row, 0);
                    String datumString = preglediTable.getValueAt(row, 1).toString(); // format: yyyy-MM-dd HH:mm:ss
                    String opombe = preglediTable.getValueAt(row, 2).toString();
                    String emso = preglediTable.getValueAt(row, 3).toString();

                    PregledSettings ps = new PregledSettings(id, datumString, opombe, emso);
                    ps.setVisible(true);
                }
            }
        });
        setVisible(true);
    }



    private void setPreglediTable(List<Object[]> dataList, String[] columns) {
        Object[][] data = dataList.toArray(new Object[0][]);
        preglediModel = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        preglediTable.setModel(preglediModel);
    }

    private void updatePreglediForDelavec(String emso) {
        String[] cols = {"ID", "Datum", "Opombe", "EMŠO"};
        List<Object[]> list = Connection.getPreglediForDelavec(emso);
        setPreglediTable(list, cols);
    }

    private void updatePreglediForTajnistvo(int id) {
        String[] cols = {"ID", "Datum", "Opombe", "EMŠO"};
        List<Object[]> list = Connection.getPreglediForTajnistvo(id);
        setPreglediTable(list, cols);
    }

    private void updateOddelkiForTajnistvo(int id) {
        String[] cols = {"ID", "Ime", "Opis", "Tajnistvo"};
        Object[][] data = Connection.getOddelkiForTajnistvo(id);
        oddelkiTable.setModel(new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void updatePreglediForOddelek(int id) {
        String[] cols = {"ID", "Datum", "Opombe", "EMŠO"};
        List<Object[]> list = Connection.getPreglediForOddelek(id);
        setPreglediTable(list, cols);
    }

    private void updateDelavciForOddelek(int id) {
        String[] cols = {"ID", "Ime", "Priimek", "EMŠO", "Oddelek"};
        Object[][] data = Connection.getDelavciForOddelek(id).toArray(new Object[0][]);
        delavciTable.setModel(new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
    }
}