package src;

import javax.swing.*;

public class TajnistvoMain extends JFrame {

    public TajnistvoMain(String email) {
        setTitle("Tajnistvo Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Pozdravljeni, tajništvo: " + email, SwingConstants.CENTER);
        add(label);

        setVisible(true);
    }
}