package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TajnistvoMain extends JFrame {

    public TajnistvoMain(int id, String ime, String glavniTajnikCa) {
        setTitle("Tajništvo Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400); // increased for table visibility
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label1 = new JLabel("Pozdravljeni, " + ime + "!", SwingConstants.CENTER);
        JLabel label3 = new JLabel("Glavni tajnik/ca: " + glavniTajnikCa, SwingConstants.CENTER);
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);
        label3.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(label1);
        panel.add(label3);
        panel.add(Box.createVerticalStrut(10));

        /// Table for oddelki (non-editable)
        String[] columnNames = {"ID", "Ime oddelka", "Opis", "Tajništvo"};
        Object[][] data = Connection.getOddelkiForTajnistvo(id);

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

// Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        panel.add(scrollPane);
        panel.add(Box.createVerticalGlue());

        add(panel);
        setVisible(true);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // double click
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int oddelekId = (int) table.getValueAt(row, 0); // hidden ID
                        new DelavciMain(oddelekId);
                    }
                }
            }
        });

    }
}

