package src;

import javax.swing.*;
import java.awt.*;

public class ZdravnikRegistrationDialog extends JDialog {

    public ZdravnikRegistrationDialog(JFrame parent) {
        super(parent, "Registracija doktorja", true);
        setSize(300, 250);
        setLocationRelativeTo(parent);

        JTextField patronField = new JTextField();
        JTextField sifraField = new JTextField();
        JTextField nazivField = new JTextField();
        JButton submitButton = new JButton("Register");

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Patron šifra:"));
        panel.add(patronField);
        panel.add(new JLabel("New šifra:"));
        panel.add(sifraField);
        panel.add(new JLabel("Naziv:"));
        panel.add(nazivField);
        panel.add(new JLabel());
        panel.add(submitButton);

        submitButton.addActionListener(e -> {
            String patron = patronField.getText().trim();
            String sifra = sifraField.getText().trim();
            String naziv = nazivField.getText().trim();

            if (patron.isEmpty() || sifra.isEmpty() || naziv.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String result = Connection.insertajZdravnika(patron, sifra, naziv);
            JOptionPane.showMessageDialog(this, result);
            if (result.equals("Uspešno narajeno!")) {
                dispose();
            }
        });

        add(panel);
        setVisible(true);
    }
}