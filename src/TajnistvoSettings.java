package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TajnistvoSettings extends JFrame {

    public TajnistvoSettings(int id, TajnistvoUpdateListener listener, TajnistvoMain parent) {
        setTitle("Nastavitve Tajništva");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField imeField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField telefonField = new JTextField();
        JTextField tajnikCaField = new JTextField();
        JTextField naslovField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> krajDropdown = new JComboBox<>();
        List<Object[]> kraji = Connection.getAllKraji();

        for (Object[] kraj : kraji) {
            krajDropdown.addItem(kraj[2] + " - " + kraj[1]);
        }
        krajDropdown.setSelectedItem(null);

        panel.add(new JLabel("Ime:"));
        panel.add(imeField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Telefon:"));
        panel.add(telefonField);
        panel.add(new JLabel("Tajnik/ca:"));
        panel.add(tajnikCaField);
        panel.add(new JLabel("Naslov:"));
        panel.add(naslovField);
        panel.add(new JLabel("Kraj:"));
        panel.add(krajDropdown);
        panel.add(new JLabel("Geslo:"));
        panel.add(passwordField);

        krajDropdown.setSelectedItem(null);

        JButton saveButton = new JButton("Shrani");
        panel.add(saveButton);

        Object[] data = Connection.getTajnistvoById(id);
        if (data != null) {
            imeField.setText((String) data[1]);
            emailField.setText((String) data[2]);
            telefonField.setText((String) data[3]);
            tajnikCaField.setText((String) data[4]);
            naslovField.setText((String) data[5]);
            passwordField.setText((String) data[7]);

            for (int i = 0; i < kraji.size(); i++) {
                if (kraji.get(i)[1].equals(data[6])) {
                    krajDropdown.setSelectedIndex(i);
                    break;
                }
            }
        }

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ime = imeField.getText().trim();
                String email = emailField.getText().trim();
                String telefon = telefonField.getText().trim();
                String glavniTajnik = tajnikCaField.getText().trim();
                String naslov = naslovField.getText().trim();
                String selectedKraj = (String) krajDropdown.getSelectedItem();
                String geslo = new String(passwordField.getPassword()).trim();

                if (ime.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ime ne sme biti prazno!");
                    return;
                }
                if (email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Email ne sme biti prazen!");
                    return;
                }
                if (telefon.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Telefon ne sme biti prazen!");
                    return;
                }
                if (glavniTajnik.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Polje 'Tajnik/ca' ne sme biti prazno!");
                    return;
                }
                if (naslov.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Naslov ne sme biti prazen!");
                    return;
                }
                if (selectedKraj == null || selectedKraj.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Kraj mora biti izbran!");
                    return;
                }
                if (geslo.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Geslo ne sme biti prazno!");
                    return;
                }

                String posta = selectedKraj.split(" - ")[0];

                Connection.updateTajnistvo(id, ime, email, telefon, glavniTajnik, naslov, posta, geslo);

                if (listener != null) {
                    listener.onTajnistvoUpdated();
                }

                dispose();
            }
        });
        JButton deleteButton = new JButton("Izbriši");
        panel.add(deleteButton);

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Ste prepričani, da želite izbrisati tajništvo?", "Potrditev brisanja", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Connection.deleteTajnistvo(id);

                dispose();
                parent.dispose();
                SwingUtilities.invokeLater(() -> new StartScreen().createAndShowGUI());
            }
        });

        add(panel);
        setVisible(true);
    }
}