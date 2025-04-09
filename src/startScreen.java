package src;

import javax.swing.*;
import java.awt.*;

public class startScreen {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new startScreen().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Login Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);

        // Left panel: Label + Šifra + Login button
        JPanel leftPanel = new JPanel(new GridLayout(3, 1));
        JLabel doktorLabel = new JLabel("DOKTOR", SwingConstants.CENTER);
        JTextField sifraField = new JTextField();
        JButton sifraLoginButton = new JButton("Login");
        leftPanel.add(doktorLabel);
        leftPanel.add(sifraField);
        leftPanel.add(sifraLoginButton);

        // Right panel: Label + Email + Password + Login button
        JPanel rightPanel = new JPanel(new GridLayout(4, 1));
        JLabel tajnistvoLabel = new JLabel("TAJNIŠTVA", SwingConstants.CENTER);
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton emailLoginButton = new JButton("Login");
        rightPanel.add(tajnistvoLabel);
        rightPanel.add(emailField);
        rightPanel.add(passwordField);
        rightPanel.add(emailLoginButton);

        // Split pane to divide left and right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);

        // Add action for doktor login
        sifraLoginButton.addActionListener(e -> {
            String sifra = sifraField.getText().trim();
            if (sifra.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter your šifra.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean isValid = Connection.checkZdravnikSifra(sifra);
            if (isValid) {
                JOptionPane.showMessageDialog(frame, "Zdravnik login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();  // Close login window
                new ZdravnikMain(sifra);  // Open new window
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid zdravnik šifra.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        emailLoginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both email and password.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean isValid = Connection.checkTajnistvoCredentials(email, password);
            if (isValid) {
                JOptionPane.showMessageDialog(frame, "Tajnistvo login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();  // Close login window
                new TajnistvoMain(email);  // Open new window
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(splitPane);
        frame.setVisible(true);
    }
}