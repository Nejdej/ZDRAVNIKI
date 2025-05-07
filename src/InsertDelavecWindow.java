package src;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

public class InsertDelavecWindow extends JFrame {

    public InsertDelavecWindow(int tajnistvoId, int izbranOddelekId, DelavciMain parent) {
        setTitle("Dodaj novega delavca");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField imeField = new JTextField();
        JTextField priimekField = new JTextField();
        JTextField emsoField = new JTextField();
        JTextField telefonField = new JTextField();

        JButton izberiSlikoBtn = new JButton("Izberi sliko");
        JLabel izbranaPotLabel = new JLabel("Ni izbrane slike.");
        final String[] base64Slika = {""};

        JComboBox<String> oddelekDropdown = new JComboBox<>();
        List<Object[]> oddelki = Connection.prikaziOddelkeVTajnistvu(tajnistvoId);

        int indexToSelect = 0;
        for (int i = 0; i < oddelki.size(); i++) {
            Object[] oddelek = oddelki.get(i);
            int id = (int) oddelek[0];
            String imeOddelka = (String) oddelek[1];
            oddelekDropdown.addItem(imeOddelka);

            if (id == izbranOddelekId) {
                indexToSelect = i;
            }
        }
        oddelekDropdown.setSelectedIndex(indexToSelect);

        // Slika izbirnik
        izberiSlikoBtn.addActionListener(ae -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Slikovne datoteke", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = fileChooser.getSelectedFile();
                    String originalName = selectedFile.getName();
                    String baseName = originalName.substring(0, originalName.lastIndexOf('.'));
                    String extension = originalName.substring(originalName.lastIndexOf('.'));

                    File dir = new File("DELAVCI");
                    if (!dir.exists()) dir.mkdirs();

                    File destination = new File(dir, originalName);
                    int counter = 1;
                    while (destination.exists()) {
                        destination = new File(dir, baseName + "_" + counter + extension);
                        counter++;
                    }

                    Files.copy(selectedFile.toPath(), destination.toPath());
                    byte[] fileBytes = Files.readAllBytes(destination.toPath());
                    base64Slika[0] = Base64.getEncoder().encodeToString(fileBytes);

                    izbranaPotLabel.setText("Shranjeno kot: " + destination.getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Napaka pri branju/shranjevanju slike.", "Napaka", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // UI layout
        mainPanel.add(new JLabel("Ime:"));
        mainPanel.add(imeField);
        mainPanel.add(Box.createVerticalStrut(5));

        mainPanel.add(new JLabel("Priimek:"));
        mainPanel.add(priimekField);
        mainPanel.add(Box.createVerticalStrut(5));

        mainPanel.add(new JLabel("EMŠO:"));
        mainPanel.add(emsoField);
        mainPanel.add(Box.createVerticalStrut(5));

        mainPanel.add(new JLabel("Telefon:"));
        mainPanel.add(telefonField);
        mainPanel.add(Box.createVerticalStrut(5));

        mainPanel.add(new JLabel("Slika delavca:"));
        mainPanel.add(izberiSlikoBtn);
        mainPanel.add(izbranaPotLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(new JLabel("Oddelek:"));
        mainPanel.add(oddelekDropdown);
        mainPanel.add(Box.createVerticalStrut(15));

        JButton insertButton = new JButton("Shrani");
        insertButton.addActionListener(e -> {
            String ime = imeField.getText();
            String priimek = priimekField.getText();
            String emso = emsoField.getText();
            String telefon = telefonField.getText();
            String slika = base64Slika[0];
            String oddelekIme = (String) oddelekDropdown.getSelectedItem();

            if (ime.isEmpty() || priimek.isEmpty() || emso.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Polja Ime, Priimek in EMŠO so obvezna!", "Napaka", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Connection.insertajDelavca(ime, priimek, emso, telefon, slika, oddelekIme);
            JOptionPane.showMessageDialog(this, "Delavec uspešno dodan!");
            parent.refreshData();
            dispose();
        });

        mainPanel.add(insertButton);
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}