import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm {
    private JButton LOGINButton;
    private JPanel panel1;
    private JTextField adminTextField;
    private JPasswordField passwordPasswordField;

    private final String host = "localhost";
    private final String port = "12345";

    public LoginForm() {
        LOGINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve username and password from the input fields
                String username = adminTextField.getText().trim();
                String password = new String(passwordPasswordField.getPassword()).trim();

                // Validate that both fields are filled
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(panel1, "Please enter both username and password.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    // initialise new client helper
                    ClientHelper helper = new ClientHelper(host, port);

                    // Send the login request and retrieve the server's response
                    String response = helper.login(username, password);

                    // Check if server response is successful
                    if (response != null && response.startsWith(String.valueOf(MessageCodes.LOGIN_SUCCESS))) {
                        JOptionPane.showMessageDialog(panel1, response, "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                        // Create new main form. Pass client helper to stay on same session.
                        JFrame mainFrame = new JFrame("Client: " + username);
                        mainFrame.setContentPane(new MainForm(username, helper).getPanel());
                        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        mainFrame.pack();
                        mainFrame.setLocationRelativeTo(null);
                        mainFrame.setVisible(true);

                        JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(panel1);
                        if (loginFrame != null) {
                            loginFrame.dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(panel1, response, "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel1, "Error during login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
    }

    public JPanel getPanel() {
        return panel1;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setAutoscrolls(false);
        panel1.setBackground(new Color(-14605015));
        panel1.setEnabled(true);
        panel1.setForeground(new Color(-14671582));
        panel1.setPreferredSize(new Dimension(350, 250));
        LOGINButton = new JButton();
        LOGINButton.setBackground(new Color(-9669251));
        LOGINButton.setEnabled(true);
        LOGINButton.setForeground(new Color(-1446673));
        LOGINButton.setText("LOGIN");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 100, 5, 100);
        panel1.add(LOGINButton, gbc);
        adminTextField = new JTextField();
        adminTextField.setAutoscrolls(true);
        adminTextField.setBackground(new Color(-11972521));
        adminTextField.setCaretColor(new Color(-14605015));
        adminTextField.setForeground(new Color(-1446673));
        adminTextField.setText("peter");
        adminTextField.setToolTipText("Username");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 20, 5, 20);
        panel1.add(adminTextField, gbc);
        passwordPasswordField = new JPasswordField();
        passwordPasswordField.setBackground(new Color(-11972521));
        passwordPasswordField.setCaretColor(new Color(-11972521));
        passwordPasswordField.setForeground(new Color(-1446673));
        passwordPasswordField.setText("password");
        passwordPasswordField.setToolTipText("Password");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 20, 5, 20);
        panel1.add(passwordPasswordField, gbc);
        final JLabel label1 = new JLabel();
        label1.setForeground(new Color(-1446673));
        label1.setText("Secure, Concurrent, Client-Server SMP");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0);
        panel1.add(label1, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
