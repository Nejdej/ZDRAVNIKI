package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class PregledSettings extends JFrame {

    private JTextArea opombeArea;
    private JTextField emsoField;

    private JComboBox<Integer> dayBox;
    private JComboBox<Integer> monthBox;
    private JComboBox<Integer> yearBox;
    private JSpinner timeSpinner;

    private String id;

    public PregledSettings(String id, String datum, String opombe, String emso) {
        this.id = id;

        setTitle("Uredi Pregled");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === DATE TIME PANEL ===
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

        // Parse existing datum
        try {
            String formattedDatum = datum.split("\\.")[0]; // Remove trailing ".0" if present
            LocalDateTime ldt = LocalDateTime.parse(formattedDatum.replace(" ", "T"));
            dayBox.setSelectedItem(ldt.getDayOfMonth());
            monthBox.setSelectedItem(ldt.getMonthValue());
            yearBox.setSelectedItem(ldt.getYear());
            timeSpinner.setValue(java.sql.Time.valueOf(ldt.toLocalTime()));

            if (ldt.isBefore(LocalDateTime.now())) {
                disableDateTimeEditing();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // === FORM PANEL ===
        JPanel form = new JPanel(new GridLayout(4, 2));
        form.add(new JLabel("Opombe:"));
        opombeArea = new JTextArea(opombe);
        JScrollPane scroll = new JScrollPane(opombeArea);
        form.add(scroll);

        form.add(new JLabel("EMŠO:"));
        emsoField = new JTextField(emso);
        emsoField.setEnabled(false);
        form.add(emsoField);

        // === BUTTONS ===
        JPanel buttons = new JPanel();
        JButton update = new JButton("Posodobi");
        JButton cancel = new JButton("Prekliči");
        JButton delete = new JButton("Izbriši");

        // === UPDATE BUTTON LOGIC ===
        update.addActionListener((ActionEvent e) -> {
            try {
                int day = (int) dayBox.getSelectedItem();
                int month = (int) monthBox.getSelectedItem();
                int year = (int) yearBox.getSelectedItem();

                Date time = (Date) timeSpinner.getValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(time);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                LocalTime localTime = LocalTime.of(hour, minute);

                LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(year, month, day), localTime);

                // Check if the datetime is in the future
                if (!dateTime.isAfter(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(PregledSettings.this,
                            "Datum in čas morata biti v prihodnosti.",
                            "Neveljaven datum", JOptionPane.WARNING_MESSAGE);
                    return; // Let the user change it again
                }

                Timestamp timestamp = Timestamp.valueOf(dateTime.withNano(0)); // trim nano
                String newOpombe = opombeArea.getText();
                String newEmso = emsoField.getText();

                Connection.updajtajPregled(id, id, timestamp, newOpombe, newEmso);
                JOptionPane.showMessageDialog(PregledSettings.this, "Pregled uspešno posodobljen.");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(PregledSettings.this,
                        "Napaka pri posodabljanju!",
                        "Napaka", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancel.addActionListener(e -> dispose());

        delete.addActionListener(e -> {
            int confirmed = JOptionPane.showConfirmDialog(PregledSettings.this,
                    "Ali ste prepričani, da želite izbrisati ta pregled?",
                    "Potrdite brisanje",
                    JOptionPane.YES_NO_OPTION);

            if (confirmed == JOptionPane.YES_OPTION) {
                Connection.deletajPregled(id);
                JOptionPane.showMessageDialog(PregledSettings.this, "Pregled uspešno izbrisan.");
                dispose();
            }
        });

        buttons.add(update);
        buttons.add(cancel);
        buttons.add(delete);

        // === LAYOUT ===
        JPanel center = new JPanel(new BorderLayout());
        center.add(dateTimePanel, BorderLayout.NORTH);
        center.add(form, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void disableDateTimeEditing() {
        dayBox.setEnabled(false);
        monthBox.setEnabled(false);
        yearBox.setEnabled(false);
        timeSpinner.setEnabled(false);
    }
}