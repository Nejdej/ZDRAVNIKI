package src;

import javax.swing.*;
import java.awt.*;
import java.util.Base64;
import java.util.List;

public class DelavciMain extends JFrame {
    private JPanel gridPanel;
    private JScrollPane scrollPane;
    private int oddelekId;
    private int tajnistvoId;

    public DelavciMain(int oddelekId, int tajnistvoId) {
        this.oddelekId = oddelekId;
        this.tajnistvoId = tajnistvoId;

        setTitle("Delavci v Oddelku");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Top panel with both buttons
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton dodajDelavcaButton = new JButton("Dodaj Delavca");
        dodajDelavcaButton.addActionListener(e -> new InsertDelavecWindow(tajnistvoId, oddelekId, this));
        leftButtons.add(dodajDelavcaButton);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton nastavitveButton = new JButton("Nastavitve");
        nastavitveButton.addActionListener(e -> {
            try {
                new OddelekSettingsWindow(oddelekId, this); // `true` indicates tajnistvo can't be changed
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Napaka pri odpiranju nastavitev tajništva: " + ex.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
            }
        });
        rightButtons.add(nastavitveButton);

        topPanel.add(leftButtons, BorderLayout.WEST);
        topPanel.add(rightButtons, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Grid of workers
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 items per row
        scrollPane = new JScrollPane(gridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

        refreshData();
        setVisible(true);
    }

    public void refreshData() {
        gridPanel.removeAll();

        List<Object[]> delavci = Connection.getDelavciForOddelek(oddelekId);

        for (Object[] d : delavci) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            card.setBackground(new Color(245, 245, 245));
            card.setPreferredSize(new Dimension(220, 180));

            JLabel name = new JLabel(d[1] + " " + d[2], SwingConstants.CENTER);
            JLabel phone = new JLabel("Telefon: " + d[4], SwingConstants.CENTER);
            JLabel kraj = new JLabel("Kraj: " + d[8], SwingConstants.CENTER);

            name.setAlignmentX(Component.CENTER_ALIGNMENT);
            phone.setAlignmentX(Component.CENTER_ALIGNMENT);
            kraj.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel imageLabel = new JLabel();
            String imageData = (String) d[5];
            if (imageData != null && !imageData.isEmpty()) {
                try {
                    ImageIcon icon = new ImageIcon(Base64.getDecoder().decode(imageData));
                    Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                } catch (Exception e) {
                    imageLabel.setText("No Image");
                }
            } else {
                imageLabel.setText("No Image");
            }
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            String emso = (String) d[3];

            JButton narociButton = new JButton("Naroči");
            narociButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            narociButton.addActionListener(e -> new NarociPregledWindow(emso));

            JButton showPreglediButton = new JButton("Prikaži preglede");
            showPreglediButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            showPreglediButton.addActionListener(e -> {
                List<Object[]> pregledi = Connection.getPreglediForDelavec(emso);
                if (pregledi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ta delavec nima nobenih planiranih pregledov.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    StringBuilder sb = new StringBuilder("Pregledi za delavca " + d[1] + " " + d[2] + ":\n\n");
                    for (Object[] p : pregledi) {
                        sb.append("ID: ").append(p[0]).append("\n")
                                .append("Datum: ").append(p[1]).append("\n")
                                .append("Opombe: ").append(p[2]).append("\n\n");
                    }

                    JTextArea textArea = new JTextArea(sb.toString());
                    textArea.setEditable(false);
                    JScrollPane sp = new JScrollPane(textArea);
                    sp.setPreferredSize(new Dimension(400, 300));
                    JOptionPane.showMessageDialog(this, sp, "Seznam pregledov", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            JButton settingsButton = new JButton("Nastavitve");
            settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            settingsButton.addActionListener(e -> {
                try {
                    new DelavecSettingsWindow(d, tajnistvoId, oddelekId, this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Napaka pri odpiranju nastavitev! " + ex.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
                }
            });

            card.add(Box.createVerticalStrut(10));
            card.add(settingsButton);
            card.add(Box.createVerticalStrut(5));
            card.add(showPreglediButton);
            card.add(Box.createVerticalStrut(10));
            card.add(narociButton);
            card.add(Box.createVerticalStrut(5));
            card.add(imageLabel);
            card.add(Box.createVerticalStrut(10));
            card.add(name);
            card.add(phone);
            card.add(kraj);
            card.add(Box.createVerticalGlue());

            gridPanel.add(card);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
}