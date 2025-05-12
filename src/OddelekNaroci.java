package src;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.time.*;
import java.util.Calendar;
import java.util.Date;

public class OddelekNaroci extends JFrame {

    private final JComboBox<Integer> dayBox = new JComboBox<>();
    private final JComboBox<Integer> monthBox = new JComboBox<>();
    private final JComboBox<Integer> yearBox = new JComboBox<>();
    private final JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
    private final JTextArea opombeArea = new JTextArea(5, 30);

    public OddelekNaroci(int oddelekId) {
        setTitle("Naroči Pregled");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // === Date & Time ===
        JPanel dateTimePanel = new JPanel();
        dateTimePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        dateTimePanel.add(new JLabel("Datum in ura:"));

        for (int i = 1; i <= 31; i++) dayBox.addItem(i);
        for (int i = 1; i <= 12; i++) monthBox.addItem(i);
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 1; i <= currentYear + 5; i++) yearBox.addItem(i);

        dateTimePanel.add(dayBox);
        dateTimePanel.add(new JLabel("."));
        dateTimePanel.add(monthBox);
        dateTimePanel.add(new JLabel("."));
        dateTimePanel.add(yearBox);
        dateTimePanel.add(new JLabel(" ob "));

        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        timeSpinner.setValue(new Date());
        dateTimePanel.add(timeSpinner);

        // === Opombe Field ===
        JPanel opombePanel = new JPanel(new BorderLayout());
        opombePanel.setBorder(BorderFactory.createTitledBorder("Opombe"));
        opombeArea.setLineWrap(true);
        opombeArea.setWrapStyleWord(true);
        opombePanel.add(new JScrollPane(opombeArea), BorderLayout.CENTER);

        // === Naroči Button ===
        JButton narociBtn = new JButton("Naroči");
        narociBtn.addActionListener(e -> {
            try {
                int day = (int) dayBox.getSelectedItem();
                int month = (int) monthBox.getSelectedItem();
                int year = (int) yearBox.getSelectedItem();

                Calendar cal = Calendar.getInstance();
                cal.setTime((Date) timeSpinner.getValue());
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.of(hour, minute));
                Timestamp timestamp = Timestamp.valueOf(dateTime.withNano(0));
                String opombe = opombeArea.getText().trim();

                boolean success = Connection.pokreniPregledeZaOddelek(oddelekId, String.valueOf(year), timestamp, opombe);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Pregled uspešno naročen!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Napaka pri naročanju.", "Napaka", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Napaka pri branju podatkov!", "Napaka", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(narociBtn);

        // === Layout ===
        setLayout(new BorderLayout(10, 10));
        add(dateTimePanel, BorderLayout.NORTH);
        add(opombePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}