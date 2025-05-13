package src;

import javax.swing.*;
import java.awt.*;

public class StartScreen {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StartScreen().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Login Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);

        // Left panel: Doktor login and registration
        JPanel leftPanel = new JPanel(new GridLayout(5, 1));
        JLabel doktorLabel = new JLabel("DOKTOR", SwingConstants.CENTER);
        JTextField sifraField = new JTextField();
        JButton sifraLoginButton = new JButton("Login");
        JLabel doktorRegistration = new JLabel("<HTML><U>Registracija</U></HTML>", SwingConstants.CENTER);
        JLabel doktorForgotPassword = new JLabel("<HTML><U>Forgot password?</U></HTML>", SwingConstants.CENTER);
        doktorRegistration.setForeground(Color.MAGENTA);
        doktorRegistration.setCursor(new Cursor(Cursor.HAND_CURSOR));
        doktorForgotPassword.setForeground(Color.BLUE);
        doktorForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftPanel.add(doktorLabel);
        leftPanel.add(sifraField);
        leftPanel.add(sifraLoginButton);
        leftPanel.add(doktorRegistration);
        leftPanel.add(doktorForgotPassword);

        // Right panel: Tajništva login and registration
        JPanel rightPanel = new JPanel(new GridLayout(6, 1));
        JLabel tajnistvoLabel = new JLabel("TAJNIŠTVA", SwingConstants.CENTER);
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton emailLoginButton = new JButton("Login");
        JLabel tajnistvoRegistration = new JLabel("<HTML><U>Registracija</U></HTML>", SwingConstants.CENTER);
        JLabel tajnistvoForgotPassword = new JLabel("<HTML><U>Forgot password?</U></HTML>", SwingConstants.CENTER);
        tajnistvoRegistration.setForeground(Color.MAGENTA);
        tajnistvoRegistration.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tajnistvoForgotPassword.setForeground(Color.BLUE);
        tajnistvoForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightPanel.add(tajnistvoLabel);
        rightPanel.add(emailField);
        rightPanel.add(passwordField);
        rightPanel.add(emailLoginButton);
        rightPanel.add(tajnistvoRegistration);
        rightPanel.add(tajnistvoForgotPassword);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(frame.getWidth() / 2);

        // DOKTOR login
        sifraLoginButton.addActionListener(e -> {
            String sifra = sifraField.getText().trim();
            if (sifra.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Prosim vnesite šifro.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String ime = Connection.checkZdravnikSifra(sifra);
            if (ime != null) {
                JOptionPane.showMessageDialog(frame, "Welcome, Dr. " + ime + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new ZdravnikMain(ime, sifra);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid šifra.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // TAJNIŠTVA login
        emailLoginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both email and password.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Object[] credentials = Connection.checkTajnistvoCredentials(email, password);
            if (credentials != null) {
                int id = (int) credentials[0];
                String ime = (String) credentials[1];
                String glavniTajnikCa = (String) credentials[2];

                JOptionPane.showMessageDialog(frame, "Welcome, " + ime + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new TajnistvoMain(id, ime, glavniTajnikCa);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clickable registration labels
        doktorRegistration.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ZdravnikRegistrationDialog(frame);
            }
        });

        tajnistvoRegistration.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new TajnistvoRegistrationDialog(frame);
            }
        });

        // Forgot password links
        doktorForgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(frame, "Obrnite se na admina.", "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        tajnistvoForgotPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new TajnistvaResetPassword(frame); // Opens the TajnistvaResetPassword dialog
            }
        });

        frame.add(splitPane);
        frame.setVisible(true);
    }
}