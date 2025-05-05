package src;

import javax.swing.*;
import java.awt.*;

public class ZdravnikSettings extends JFrame {

    public ZdravnikSettings(String currentNaziv, String currentSifra, ZdravnikUpdateListener listener) {
        setTitle("Zdravnik Nastavitve");
        setSize(300, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10));

        JTextField newSifraField = new JTextField();
        newSifraField.setBorder(BorderFactory.createTitledBorder("Nova Šifra"));

        JTextField newNazivField = new JTextField();
        newNazivField.setBorder(BorderFactory.createTitledBorder("Nov Naziv"));

        JButton updateButton = new JButton("UPDATE");
        updateButton.addActionListener(e -> {
            String newSifra = newSifraField.getText().trim();
            String newNaziv = newNazivField.getText().trim();

            if (newSifra.isEmpty() && newNaziv.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Obe polji ne moreta biti prazni.");
                return;
            }

            if (newSifra.isEmpty()) newSifra = currentSifra;
            if (newNaziv.isEmpty()) newNaziv = currentNaziv;

            Connection.updateZdravnik(currentSifra, newSifra, currentNaziv, newNaziv);
            JOptionPane.showMessageDialog(this, "Zdravnik uspešno posodobljen.");
            dispose();

            // Notify Main Window
            if (listener != null) {
                listener.onZdravnikUpdated(newNaziv, newSifra);
            }
        });

        JButton deleteButton = new JButton("DELETE");
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Ali si prepričan/a, da želiš izbrisati zdravnika?", "Potrdi brisanje", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Connection.deleteZdravnik(currentSifra);
                JOptionPane.showMessageDialog(this, "Zdravnik izbrisan.");
                dispose();
                System.exit(0); // or go back to login screen if you have one
            }
        });

        add(newSifraField);
        add(newNazivField);
        add(updateButton);
        add(deleteButton);
    }
}