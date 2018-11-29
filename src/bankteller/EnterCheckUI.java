package bankteller;

import atm.AccountSelectUI;
import dao.*;
import model.Account;
import model.Transaction;
import model.TransactionType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Date;

public class EnterCheckUI extends JPanel implements ActionListener {
    private JPanel usernamePanel;

    private JPanel passwordPanel;

    private JPanel loginPanel;

    private JTextField usernameText;

    private JPasswordField passwordText;

    private JButton loginButton;

    private JPanel describePanel;

    private JPanel checkNoPanel;

    private JPanel confirmPanel;

    private JTextField checkNumberText;

    private JButton confirmButton;

    private Account account;

    public EnterCheckUI(){
        JOptionPane.showMessageDialog(this,"Please let customer login");
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

        // Initial describe area
        describePanel = new JPanel();
        describePanel.add(new JLabel("Please insert check number!"));

        // Initial input area
        checkNoPanel = new JPanel();
        checkNoPanel.add(new JLabel("Check Number:"));
        checkNumberText = new JTextField(20);
        checkNoPanel.add(checkNumberText);

        // Initial confirm area
        confirmPanel = new JPanel();
        confirmButton = new JButton("Confirm");
        confirmPanel.add(confirmButton);

        this.add(usernamePanel);
        this.add(passwordPanel);
        this.add(loginPanel);
        this.setLayout(new GridLayout(3,1));
        this.setVisible(true);

        loginButton.addActionListener(this);
        confirmButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Login")){
            // Access database
            CustomerDao customerDao = new CustomerDao();
            AccountDao accountDao = new AccountDao();
            // Create a PinDao to verify password
            PinDao pinDao = new PinDao();
            // If current customer is in table
            if (customerDao.queryCustomerByTax(usernameText.getText()) != null) {
                // Check pin
                if (pinDao.verifyPin(usernameText.getText(), passwordText.getText())) {
                    // If login success
                    String accountid = JOptionPane.showInputDialog(new AccountSelectUI(usernameText.getText(), false));
                    if(accountid == null)return;
                    this.account = accountDao.queryAccountById(Integer.valueOf(accountid));
                    this.removeAll();
                    this.add(describePanel);
                    this.add(checkNoPanel);
                    this.add(confirmPanel);
                    this.setLayout(new GridLayout(3,1));
                    this.setVisible(true);
                    this.repaint();
                    this.revalidate();
                } else {
                    JOptionPane.showMessageDialog(this, "Wrong password!");
                }
            }
            // If the pin of the customer is wrong
            else {
                JOptionPane.showMessageDialog(this, "User does not exit!");
            }
        }
        else if(e.getActionCommand().equals("Confirm")){
            // Judge if current check is submitted
            TransactionDao transactionDao = new TransactionDao();
            Transaction[] transactions = transactionDao.queryTransactionsByCheckNumber(checkNumberText.getText());
            if(transactions == null || transactions.length == 2){
                JOptionPane.showMessageDialog(this,"Check number invalid!");
                return;
            }
            Transaction check = transactions[0];
            // Firstly create a transaction object
            BigDecimal current_money = check.getMoney();

            // Update the amount of current account
            // Current money
            BigDecimal currentBalance = account.getBalance();
            currentBalance = currentBalance.add(current_money);
            AccountDao accountDao = new AccountDao();
            accountDao.updateBalance(account.getId(), currentBalance);
            account.setBalance(currentBalance);

            Transaction transaction = new Transaction();
            // transaction.setTran_date(new Date());
            transaction.setTran_date(TimeDao.getCurrentTime());
            transaction.setTransactionType(TransactionType.ENTER_CHECK);
            transaction.setCustomerId(usernameText.getText());
            transaction.setFrom_id(check.getFrom_id());
            transaction.setTo_id(account.getId());
            transaction.setMoney(current_money);
            transaction.setActual_money(current_money);
            transaction.setFee(new BigDecimal("0"));
            transaction.setCheck_number(null);

            // Access to database and add a new transaction
            transactionDao.addTransaction(transaction);

            JOptionPane.showMessageDialog(this, "Check is successfully submitted!");
            this.setVisible(false);
        }
    }
}
