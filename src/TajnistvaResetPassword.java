package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TajnistvaResetPassword extends JDialog {

    public TajnistvaResetPassword(JFrame parent) {
        super(parent, "Reset Password for Tajništva", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel newPasswordLabel = new JLabel("New Password:");
        JPasswordField newPasswordField = new JPasswordField();
        JButton submitButton = new JButton("Submit");

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(newPasswordLabel);
        panel.add(newPasswordField);
        panel.add(new JLabel());
        panel.add(submitButton);

        submitButton.addActionListener((ActionEvent e) -> {
            String email = emailField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();

            if (email.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call the resetPassword function here with email and newPassword
            String result = Connection.resetPasswordForTajnistvo(email, newPassword);
            JOptionPane.showMessageDialog(this, result, "Password Reset", JOptionPane.INFORMATION_MESSAGE);

            // Close the dialog after submission
            dispose();
        });

        add(panel);
        setVisible(true);
    }
}