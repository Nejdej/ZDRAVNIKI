package src;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OddelekSettingsWindow extends JFrame {
    private JTextField imeField;
    private JTextArea opisArea;
    private JTextField tajnistvoField;

    public OddelekSettingsWindow (int oddelekId, DelavciMain parent) {
        setTitle("Nastavitve Oddelka");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        List<String> oddelekData = Connection.prikaziOddelek(oddelekId);
        if (oddelekData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Napaka pri nalaganju oddelka.");
            dispose();
            return;
        }

        JPanel form = new JPanel(new GridLayout(6, 1, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        imeField = new JTextField(oddelekData.get(1));
        opisArea = new JTextArea(oddelekData.get(2));
        opisArea.setLineWrap(true);
        opisArea.setWrapStyleWord(true);

        tajnistvoField = new JTextField(oddelekData.get(3));
        tajnistvoField.setEditable(false);

        form.add(new JLabel("Ime:"));
        form.add(imeField);
        form.add(new JLabel("Opis:"));
        form.add(new JScrollPane(opisArea));
        form.add(new JLabel("Tajnistvo:"));
        form.add(tajnistvoField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateBtn = new JButton("Posodobi");
        JButton deleteBtn = new JButton("Izbriši");

        updateBtn.addActionListener(e -> {
            String ime = imeField.getText().trim();
            String opis = opisArea.getText().trim();
            String tajnistvoIme = tajnistvoField.getText().trim();

            if (ime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ime ne sme biti prazno!");
                return;
            }

            Connection.updajtajOddelek(oddelekId, ime, opis, tajnistvoIme);
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Si prepričan, da želiš izbrisati oddelek?", "Potrditev", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Connection.deletajOddelek(oddelekId);
                    TajnistvoMain.refreshTable();
                } catch (Exception ex) {
                    ex.printStackTrace(); // check console for stack trace
                } finally {
                    parent.dispose(); // closes DelavciMain
                    dispose();        // closes this settings window
                }
            }
        });

        buttons.add(updateBtn);
        buttons.add(deleteBtn);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        setVisible(true);
    }
}