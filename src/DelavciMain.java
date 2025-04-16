package src;

import javax.swing.*;
import java.awt.*;
import java.util.Base64;
import java.util.List;

public class DelavciMain extends JFrame {

    public DelavciMain(int oddelekId) {
        setTitle("Delavci v Oddelku");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        List<Object[]> delavci = Connection.getDelavciForOddelek(oddelekId);

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 items per row

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

            // Optional: Show image if exists (assuming base64 or URL or null)
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

            card.add(Box.createVerticalStrut(5));
            card.add(imageLabel);
            card.add(Box.createVerticalStrut(10));
            card.add(name);
            card.add(phone);
            card.add(kraj);
            card.add(Box.createVerticalGlue());

            gridPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);
        setVisible(true);
    }
}