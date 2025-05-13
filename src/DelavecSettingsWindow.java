package src;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

public class DelavecSettingsWindow extends JFrame {

    public DelavecSettingsWindow(Object[] delavecData, int tajnistvoId, int oddelekId, DelavciMain parent) {
        setTitle("Nastavitve Delavca");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        String ime = (String) delavecData[1];
        String priimek = (String) delavecData[2];
        String currentEmso = (String) delavecData[3];
        String telefon = (String) delavecData[4];
        String slikaBase64 = (String) delavecData[5];

        JLabel imageLabel = new JLabel();
        if (slikaBase64 != null && !slikaBase64.isEmpty()) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(slikaBase64);
                ImageIcon icon = new ImageIcon(imageBytes);
                Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                imageLabel.setText("Slika ni na voljo");
            }
        } else {
            imageLabel.setText("Slika ni na voljo");
        }

        JButton izberiSlikoBtn = new JButton("Izberi sliko");
        JLabel izbranaPotLabel = new JLabel("Ni izbrane slike.");
        final String[] base64Slika = {slikaBase64};  // fallback to original image if no new selected

        izberiSlikoBtn.addActionListener(ae -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Slikovne datoteke", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = fileChooser.getSelectedFile();
                    byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                    base64Slika[0] = Base64.getEncoder().encodeToString(fileBytes);

                    ImageIcon icon = new ImageIcon(fileBytes);
                    Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));

                    izbranaPotLabel.setText("Izbrana slika: " + selectedFile.getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Napaka pri branju/shranjevanju slike.", "Napaka", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JTextField imeField = new JTextField(ime);
        JTextField priimekField = new JTextField(priimek);
        JTextField emsoField = new JTextField(currentEmso);
        JTextField telefonField = new JTextField(telefon);

        JComboBox<String> oddelekDropdown = new JComboBox<>();
        List<Object[]> oddelki = Connection.prikaziOddelkeVTajnistvu(tajnistvoId);
        String selectedOddelekIme = null;

        for (Object[] oddelek : oddelki) {
            String imeOddelka = (String) oddelek[1];
            oddelekDropdown.addItem(imeOddelka);
            int id = (int) oddelek[0];
            if (id == oddelekId) selectedOddelekIme = imeOddelka;
        }
        if (selectedOddelekIme != null) {
            oddelekDropdown.setSelectedItem(selectedOddelekIme);
        }

        // Add fields
        mainPanel.add(new JLabel("Ime:"));
        mainPanel.add(imeField);
        mainPanel.add(new JLabel("Priimek:"));
        mainPanel.add(priimekField);
        mainPanel.add(new JLabel("EMŠO:"));
        mainPanel.add(emsoField);
        mainPanel.add(new JLabel("Telefon:"));
        mainPanel.add(telefonField);
        mainPanel.add(new JLabel("Oddelek:"));
        mainPanel.add(oddelekDropdown);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(imageLabel);
        mainPanel.add(izberiSlikoBtn);
        mainPanel.add(izbranaPotLabel);

        JButton updateButton = new JButton("Posodobi");
        updateButton.addActionListener(e -> {
            String newIme = imeField.getText().trim();
            String newPriimek = priimekField.getText().trim();
            String newEmso = emsoField.getText().trim();
            String newTelefon = telefonField.getText().trim();
            String oddelekIme = (String) oddelekDropdown.getSelectedItem();

            if (newIme.isEmpty() || newPriimek.isEmpty() || newEmso.isEmpty() || newTelefon.isEmpty() || oddelekIme == null) {
                JOptionPane.showMessageDialog(this, "Vsa polja razen slike morajo biti izpolnjena.", "Napaka", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // EMŠO validation: exactly 13 digits
            if (!newEmso.matches("\\d{13}")) {
                JOptionPane.showMessageDialog(this, "EMŠO mora vsebovati točno 13 številk.", "Napaka", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Connection.updajtajDelavca(
                    currentEmso, newEmso, newIme, newPriimek, newTelefon, base64Slika[0], oddelekIme
            );

            JOptionPane.showMessageDialog(this, "Delavec uspešno posodobljen!");
            parent.refreshData();
            dispose();
        });

        JButton deleteButton = new JButton("Izbriši");
        deleteButton.setForeground(Color.RED);
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Ali si prepričan, da želiš izbrisati tega delavca?", "Potrditev brisanja", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Connection.deletajDelavca(currentEmso);
                JOptionPane.showMessageDialog(this, "Delavec uspešno izbrisan!");
                parent.refreshData();
                dispose();
            }
        });

        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(updateButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(deleteButton);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}