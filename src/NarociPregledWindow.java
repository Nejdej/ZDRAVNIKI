package src;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class NarociPregledWindow extends JFrame {

    public NarociPregledWindow(String emso) {
        setTitle("Naroči Pregled");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Date and Time Panel
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dateTimePanel.add(new JLabel("Datum in ura:"));

        JComboBox<Integer> dayBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayBox.addItem(Integer.valueOf(i));
        dateTimePanel.add(dayBox);
        dateTimePanel.add(new JLabel("."));

        JComboBox<Integer> monthBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) monthBox.addItem(Integer.valueOf(i));
        dateTimePanel.add(monthBox);
        dateTimePanel.add(new JLabel("."));

        JComboBox<Integer> yearBox = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i <= currentYear + 5; i++) yearBox.addItem(Integer.valueOf(i));
        dateTimePanel.add(yearBox);
        dateTimePanel.add(new JLabel(" ob "));

        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new Date());
        dateTimePanel.add(timeSpinner);

        add(dateTimePanel);

        // Opombe
        JPanel opombePanel = new JPanel(new BorderLayout());
        opombePanel.setBorder(BorderFactory.createTitledBorder("Opombe"));
        JTextArea opombeArea = new JTextArea(3, 20);
        opombeArea.setLineWrap(true);
        opombeArea.setWrapStyleWord(true);
        opombePanel.add(new JScrollPane(opombeArea), BorderLayout.CENTER);
        add(opombePanel);

        // EMŠO
        JPanel emsoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emsoPanel.add(new JLabel("EMŠO:"));
        JTextField emsoField = new JTextField(emso, 15);
        emsoField.setEditable(false);
        emsoPanel.add(emsoField);
        add(emsoPanel);

        // Confirm button
        JButton confirmButton = new JButton("Potrdi");
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.addActionListener(e -> {
            try {
                String opombe = opombeArea.getText().trim();

                int day = (int) dayBox.getSelectedItem();
                int month = (int) monthBox.getSelectedItem();
                int year = (int) yearBox.getSelectedItem();

                Date time = (Date) timeSpinner.getValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(time);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                LocalDateTime localDateTime = LocalDateTime.of(
                        year, month, day,
                        hour, minute
                );

                Timestamp datum = Timestamp.valueOf(localDateTime);
                Connection.insertajPregled(String.valueOf(year), datum, opombe, emso);

                JOptionPane.showMessageDialog(this, "Pregled uspešno dodan!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Napaka pri vnosu pregleda!", "Napaka", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        add(confirmButton);
        setVisible(true);
    }
}