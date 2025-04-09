package src;

import javax.swing.*;

public class ZdravnikMain extends JFrame {

    public ZdravnikMain(String sifra) {
        setTitle("Zdravnik Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Pozdravljen, zdravnik: " + sifra, SwingConstants.CENTER);
        add(label);

        setVisible(true);
    }
}