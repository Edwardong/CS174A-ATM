package bankteller;

import Util.MoneyUtil;
import atm.AccountSelectUI;
import atm.MenuUI;
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

public class WriteCheckUI extends JPanel implements ActionListener {
    private JPanel usernamePanel;

    private JPanel passwordPanel;

    private JPanel loginPanel;

    private JTextField usernameText;

    private JPasswordField passwordText;

    private JButton loginButton;

    private JPanel describePanel;

    private JPanel amountPanel;

    private JPanel confirmPanel;

    private JTextField amountText;

    private JButton confirmButton;

    private Account account;

    public WriteCheckUI(){
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
        describePanel.add(new JLabel("Please insert the amount of money!"));

        // Initial input area
        amountPanel = new JPanel();
        amountPanel.add(new JLabel("Amount:"));
        amountText = new JTextField(20);
        amountPanel.add(amountText);

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
                    String accountid = JOptionPane.showInputDialog(new AccountSelectUI(usernameText.getText(), false));
                    if(accountid == null)return;
                    this.account = accountDao.queryAccountById(Integer.valueOf(accountid));
                    this.removeAll();
                    this.add(describePanel);
                    this.add(amountPanel);
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
            // Firstly verify money input is correct
            if (MoneyUtil.verifyMoney(amountText.getText())) {
                // Firstly create a transaction object
                BigDecimal current_money = new BigDecimal(amountText.getText());

                // Check if the balance is enough
                if(current_money.compareTo(account.getBalance()) <= 0){
                    BigDecimal currentBalance = account.getBalance();

                    // Update the amount of current account
                    currentBalance = currentBalance.subtract(current_money);
                    AccountDao accountDao = new AccountDao();
                    accountDao.updateBalance(account.getId(), currentBalance);
                    account.setBalance(currentBalance);

                    Transaction transaction = new Transaction();
                    // transaction.setTran_date(new Date());
                    transaction.setTran_date(TimeDao.getCurrentTime());
                    transaction.setTransactionType(TransactionType.WRITE_CHECK);
                    transaction.setCustomerId(usernameText.getText());
                    transaction.setFrom_id(account.getId());
                    transaction.setTo_id(-1);
                    transaction.setMoney(current_money);
                    transaction.setActual_money(current_money);
                    transaction.setFee(new BigDecimal("0"));
                    transaction.setCheck_number(String.valueOf(IDCreator.getNextId()));

                    // Access to database and add a new transaction
                    TransactionDao transactionDao = new TransactionDao();
                    transactionDao.addTransaction(transaction);

                    // Finally check if current account is closed
                    if(currentBalance.compareTo(new BigDecimal("0.01")) < 0){
                        accountDao.updateState(account.getId(), true);
                        account.setClosed(true);
                    }

                    JOptionPane.showMessageDialog(this, "Check is successfully written! Check Number : " + transaction.getCheck_number());
                    this.setVisible(false);
                }else{
                    JOptionPane.showMessageDialog(this, "Your balance is not enough! Balance : $" + account.getBalance().setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                }
            } else {
                JOptionPane.showMessageDialog(this, "The format of money is wrong!");
            }
        }
    }
}
