package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KrajUpdateDeleteWindow extends JFrame {
    private int krajId;
    private String krajPosta;
    private JTextField imeField;
    private JTextField postaField;

    public KrajUpdateDeleteWindow(int krajId, String krajPosta) {
        this.krajId = krajId;
        this.krajPosta = krajPosta;

        setTitle("Update or Delete Kraj");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        // Create text fields for updating
        panel.add(new JLabel("Ime kraja:"));
        imeField = new JTextField();
        panel.add(imeField);

        panel.add(new JLabel("Posta:"));
        postaField = new JTextField();
        panel.add(postaField);

        // Set current values in the fields
        imeField.setText(Connection.getAllKraji().stream()
                .filter(row -> row[2].equals(krajPosta))
                .map(row -> (String) row[1])
                .findFirst().orElse(""));

        postaField.setText(krajPosta);

        // Update button
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ime = imeField.getText();
                String posta = postaField.getText();

                if (!ime.isEmpty() && !posta.isEmpty()) {
                    // Update the Kraj using the provided updateKraj function
                    Connection.updateKraj(krajPosta, ime, posta);
                    JOptionPane.showMessageDialog(KrajUpdateDeleteWindow.this, "Kraj updated successfully.");
                    // Refresh the table in the main window
                    KrajiWindow.refreshTable();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(KrajUpdateDeleteWindow.this, "Fields cannot be empty.");
                }
            }
        });

        // Delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(KrajUpdateDeleteWindow.this,
                        "Are you sure you want to delete this Kraj?", "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Connection.deleteKraj(krajPosta);
                    JOptionPane.showMessageDialog(KrajUpdateDeleteWindow.this, "Kraj deleted successfully.");
                    // Refresh the table in the main window
                    KrajiWindow.refreshTable();
                    dispose();
                }
            }
        });

        panel.add(updateButton);
        panel.add(deleteButton);

        add(panel);
        setVisible(true);
    }
}