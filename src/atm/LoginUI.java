package atm;

import dao.AccountDao;
import dao.CustomerDao;
import dao.PinDao;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI implements ActionListener {
    private JFrame loginFrame;

    private JPanel welcomePanel;

    private JPanel usernamePanel;

    private JPanel passwordPanel;

    private JPanel loginPanel;

    private JPanel accountsPanel;

    private JTextField usernameText;

    private JPasswordField passwordText;

    private JButton loginButton;

    // Initial the login ui
    public LoginUI() {
        // Set login title
        loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Initial welcome area
        welcomePanel = new JPanel();
        welcomePanel.add(new JLabel("Welcome to ATM! \t"));

        // Initial the username input area
        usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("Tax identification number"));
        usernameText = new JTextField(20);
        usernamePanel.add(usernameText);

        // Initial the password input area
        passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("PIN"));
        passwordText = new JPasswordField(20);
        passwordPanel.add(passwordText);

        // Initial the login button
        loginPanel = new JPanel();
        loginButton = new JButton("Login");
        loginPanel.add(loginButton);

        // Initial the Account Panel
        accountsPanel = new JPanel();

        loginFrame.add(welcomePanel);
        loginFrame.add(usernamePanel);
        loginFrame.add(passwordPanel);
        loginFrame.add(loginPanel);
        loginFrame.add(accountsPanel);

        loginFrame.setVisible(true);
        loginFrame.setBounds(300, 300, 800, 400);
        loginFrame.setLayout(new GridLayout(5, 1));

        loginButton.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // If the login button is pressed
        if (e.getActionCommand().equals("Login")) {
            // Create a CustomerDao to access database
            CustomerDao customerDao = new CustomerDao();
            // Create a PinDao to verify password
            PinDao pinDao = new PinDao();
            // If current customer is in table
            if (customerDao.queryCustomerByTax(usernameText.getText()) != null) {
                // Check pin
                if (pinDao.verifyPin(usernameText.getText(), passwordText.getText())) {
                    JOptionPane.showMessageDialog(loginFrame, "Login success!");
                    MenuUI newMenu = new MenuUI(usernameText.getText());
                    loginFrame.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Wrong password!");
                }
            }
            // If the pin of the customer is wrong
            else {
                JOptionPane.showMessageDialog(loginFrame, "User does not exit!");
            }
        }
    }
}
