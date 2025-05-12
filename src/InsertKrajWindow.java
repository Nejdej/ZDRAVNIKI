package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InsertKrajWindow extends JFrame {
    private JTextField imeField;
    private JTextField postaField;

    public InsertKrajWindow() {
        setTitle("Insert New Kraj");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("Ime kraja:"));
        imeField = new JTextField();
        panel.add(imeField);

        panel.add(new JLabel("Posta:"));
        postaField = new JTextField();
        panel.add(postaField);

        // Insert button
        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ime = imeField.getText();
                String posta = postaField.getText();

                if (!ime.isEmpty() && !posta.isEmpty()) {
                    // Insert the Kraj using the provided insertKraj function
                    Connection.insertKraj(ime, posta);
                    JOptionPane.showMessageDialog(InsertKrajWindow.this, "Kraj inserted successfully.");
                    // Refresh the table in the main window
                    KrajiWindow.refreshTable();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(InsertKrajWindow.this, "Fields cannot be empty.");
                }
            }
        });

        panel.add(insertButton);

        add(panel);
        setVisible(true);
    }
}