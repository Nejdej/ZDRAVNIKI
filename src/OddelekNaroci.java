package src;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class OddelekNaroci extends JFrame {

    private JComboBox<Integer> dayBox, monthBox, yearBox;
    private JSpinner timeSpinner;
    private JTextArea opombeArea;

    public OddelekNaroci(int oddelekId) {
        setTitle("Naroči Pregled");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === Date Time Panel ===
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dateTimePanel.add(new JLabel("Datum in ura:"));

        dayBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayBox.addItem(i);
        dateTimePanel.add(dayBox);
        dateTimePanel.add(new JLabel("."));

        monthBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) monthBox.addItem(i);
        dateTimePanel.add(monthBox);
        dateTimePanel.add(new JLabel("."));

        yearBox = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 1; i <= currentYear + 5; i++) yearBox.addItem(i);
        dateTimePanel.add(yearBox);
        dateTimePanel.add(new JLabel(" ob "));

        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new Date());
        dateTimePanel.add(timeSpinner);

        // === Form Panel ===
        JPanel form = new JPanel(new GridLayout(4, 2));

        form.add(new JLabel("Opombe:"));
        opombeArea = new JTextArea();
        form.add(new JScrollPane(opombeArea));

        // === Button ===
        JButton naroci = new JButton("Naroči");

        naroci.addActionListener(e -> {
            try {
                int day = (int) dayBox.getSelectedItem();
                int month = (int) monthBox.getSelectedItem();
                int year = (int) yearBox.getSelectedItem();

                Date time = (Date) timeSpinner.getValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(time);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

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
        buttonPanel.add(naroci);

        // === Layout ===
        add(dateTimePanel, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}