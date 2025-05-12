package src;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TajnistvoRegistrationDialog extends JDialog {

    public TajnistvoRegistrationDialog(JFrame parent) {
        super(parent, "Registracija tajnistva.", true);
        setSize(350, 350);
        setLocationRelativeTo(parent);

        JTextField imeField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField telefonField = new JTextField();
        JTextField glavniField = new JTextField();
        JTextField naslovField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton submitButton = new JButton("Register");

        JComboBox<String> postaDropdown = new JComboBox<>();
        List<Object[]> kraji = Connection.getAllKraji();
        for (Object[] kraj : kraji) {
            postaDropdown.addItem(kraj[2] + " - " + kraj[1]); // assuming [2] = postna_st, [1] = kraj
        }
        postaDropdown.setSelectedItem(null);

        JPanel panel = new JPanel(new GridLayout(8, 2));
        panel.add(new JLabel("Ime:"));
        panel.add(imeField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Telefon:"));
        panel.add(telefonField);
        panel.add(new JLabel("Glavni tajnik/ca:"));
        panel.add(glavniField);
        panel.add(new JLabel("Naslov:"));
        panel.add(naslovField);
        panel.add(new JLabel("Pošta:"));
        panel.add(postaDropdown);
        panel.add(new JLabel("Geslo:"));
        panel.add(passField);
        panel.add(new JLabel());
        panel.add(submitButton);

        submitButton.addActionListener(e -> {
            String ime = imeField.getText().trim();
            String email = emailField.getText().trim();
            String telefon = telefonField.getText().trim();
            String glavni = glavniField.getText().trim();
            String naslov = naslovField.getText().trim();
            String selectedKraj = (String) postaDropdown.getSelectedItem();
            String pass = new String(passField.getPassword()).trim();

            if (ime.isEmpty() || email.isEmpty() || telefon.isEmpty() || glavni.isEmpty() ||
                    naslov.isEmpty() || selectedKraj == null || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String posta = selectedKraj.split(" - ")[0];

            boolean success = Connection.insertajTajnistvo(ime, email, telefon, glavni, naslov, posta, pass);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Check if pošta exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panel);
        setVisible(true);
    }
}