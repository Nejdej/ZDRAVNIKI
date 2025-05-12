package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class KrajiWindow extends JFrame {
    private static DefaultTableModel model;
    private static JTable table;
    private int tajnistvoId;

    public KrajiWindow(int tajnistvoId) {
        this.tajnistvoId = tajnistvoId;

        setTitle("Kraji");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add Insert Kraj Button
        JButton insertButton = new JButton("Insert Kraj");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InsertKrajWindow(); // Open the InsertKrajWindow when clicked
            }
        });
        panel.add(insertButton);

        // Table setup
        String[] columnNames = {"ID", "Ime kraja", "Posta"};
        List<Object[]> data = Connection.getAllKraji();
        Object[][] krajiData = data.toArray(new Object[0][]);

        model = new DefaultTableModel(krajiData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        // Double-click logic to open update/delete window
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int krajId = (int) table.getValueAt(row, 0);
                        String posta = (String) table.getValueAt(row, 2);
                        new KrajUpdateDeleteWindow(krajId, posta); // Open the update/delete window
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        panel.add(scrollPane);

        // Add to main frame
        add(panel);
        setVisible(true);
    }

    // Refresh the table after update/delete operation
    public static void refreshTable() {
        List<Object[]> data = Connection.getAllKraji();
        Object[][] krajiData = data.toArray(new Object[0][]);
        model.setDataVector(krajiData, new Object[]{"ID", "Ime kraja", "Posta"});
    }
}