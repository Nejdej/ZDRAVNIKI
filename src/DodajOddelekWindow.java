package src;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DodajOddelekWindow extends JFrame {
    public DodajOddelekWindow(int tajnistvoId, TajnistvoMain parent) {
        setTitle("Dodaj oddelek");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 1, 10, 10)); // Adjusted rows for layout clarity

        JLabel imeLabel = new JLabel("Ime oddelka:");
        JTextField imeField = new JTextField();

        JLabel opisLabel = new JLabel("Opis oddelka:");
        JTextField opisField = new JTextField();

        JLabel tajnistvoLabel = new JLabel("Tajništvo:");
        JComboBox<String> tajnistvoBox = new JComboBox<>();
        ArrayList<Object[]> tajnistvaList = (ArrayList<Object[]>) Connection.getTajnistva();

        int preselectIndex = -1;

        for (int i = 0; i < tajnistvaList.size(); i++) {
            Object[] row = tajnistvaList.get(i);
            tajnistvoBox.addItem((String) row[1]); // Add name
            if (((Integer) row[0]) == tajnistvoId) {
                preselectIndex = i; // Save index to preselect
            }
        }

        if (preselectIndex != -1) {
            tajnistvoBox.setSelectedIndex(preselectIndex);
        }

        JButton confirmButton = new JButton("Potrdi");
        confirmButton.addActionListener(e -> {
            String ime = imeField.getText().trim();
            String opis = opisField.getText().trim();
            int selectedIndex = tajnistvoBox.getSelectedIndex();

            if (ime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ime oddelka ne sme biti prazno.");
                return;
            }

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Izberite tajništvo.");
                return;
            }

            Object[] selectedTajnistvo = tajnistvaList.get(selectedIndex);
            String tajnistvoIme = (String) selectedTajnistvo[1];

            Connection.insertajOddelek(ime, opis, tajnistvoIme);
            parent.refreshTable();
            dispose();
        });

        add(imeLabel);
        add(imeField);
        add(opisLabel);
        add(opisField);
        add(tajnistvoLabel);
        add(tajnistvoBox);
        add(confirmButton);

        setVisible(true);
    }
}