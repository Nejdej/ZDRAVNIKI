package src;

import javax.swing.*;
import java.awt.*;

public class TajnistvoMain extends JFrame {

    public TajnistvoMain(int id, String ime, String glavniTajnikCa) {
        setTitle("Tajništvo Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Use a vertical box layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label1 = new JLabel("Pozdravljeni, " + ime + "!", SwingConstants.CENTER);
        JLabel label3 = new JLabel("Glavni tajnik/ca: " + glavniTajnikCa, SwingConstants.CENTER);

        label1.setAlignmentX(Component.CENTER_ALIGNMENT);
        label3.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(label1);
        panel.add(label3);
        panel.add(Box.createVerticalGlue());

        add(panel);
        setVisible(true);
    }
}