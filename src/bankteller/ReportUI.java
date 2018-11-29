package bankteller;

import dao.AccountDao;
import dao.CustomerDao;
import dao.PinDao;
import model.Account;
import model.AccountType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class ReportUI extends JPanel implements ActionListener {
    private JPanel usernamePanel;

    private JPanel passwordPanel;

    private JPanel loginPanel;

    private JTextField usernameText;

    private JPasswordField passwordText;

    private JButton loginButton;

    private JPanel accountsPanel;

    private Account[] accounts;

    public ReportUI() {
        JOptionPane.showMessageDialog(this, "Please let customer login");
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

        this.add(usernamePanel);
        this.add(passwordPanel);
        this.add(loginPanel);
        this.setLayout(new GridLayout(3, 1));
        this.setVisible(true);

        loginButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Login")) {
            // Access database
            CustomerDao customerDao = new CustomerDao();
            AccountDao accountDao = new AccountDao();
            // Create a PinDao to verify password
            PinDao pinDao = new PinDao();
            // If current customer is in table
            if (customerDao.queryCustomerByTax(usernameText.getText()) != null) {
                // Check pin
                if (pinDao.verifyPin(usernameText.getText(), passwordText.getText())) {
                    accountsPanel = new JPanel();

                    // Get all accounts belong to current customer
                    accounts = accountDao.queryAccountByCustomerId(usernameText.getText());
                    if (accounts != null && accounts.length != 0) {
                        accountsPanel.add(new Label("Account Id"));
                        accountsPanel.add(new Label("Account Type"));
                        accountsPanel.add(new Label("Bank Branch Name"));
                        accountsPanel.add(new Label("Balance"));
                        accountsPanel.add(new Label("Closed"));
                        int count = 1;
                        for (int i = 0; i < accounts.length; i++) {
                            accountsPanel.add(new Label(String.valueOf(accounts[i].getId())));
                            accountsPanel.add(new Label(String.valueOf(accounts[i].getType())));
                            accountsPanel.add(new Label(accounts[i].getBank_branch_name()));
                            accountsPanel.add(new Label(accounts[i].getBalance().setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                            accountsPanel.add(new Label(accounts[i].getClosed() ? "true" : "false"));
                            count++;
                        }
                        accountsPanel.setLayout(new GridLayout(count, 5));
                        accountsPanel.setVisible(true);

                        this.removeAll();
                        this.add(accountsPanel);
                        this.setVisible(true);
                        this.setLayout(new GridLayout(2, 1));
                        this.repaint();
                        this.revalidate();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Wrong password!");
                }
            }
            // If the pin of the customer is wrong
            else {
                JOptionPane.showMessageDialog(this, "User does not exit!");
            }
        }
    }
}
