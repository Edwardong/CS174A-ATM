package bankteller;

import atm.AccountSelectUI;
import dao.AccountDao;
import dao.CustomerDao;
import dao.PinDao;
import dao.TransactionDao;
import model.Account;
import model.Customer;
import model.Transaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MonthlyUI extends JPanel implements ActionListener {
    private JPanel wholePanel;

    private JScrollPane scrollPane;

    private JPanel usernamePanel;

    private JPanel passwordPanel;

    private JPanel loginPanel;

    private JTextField usernameText;

    private JPasswordField passwordText;

    private JButton loginButton;

    private JPanel primaryPanel;

    private JPanel coownerPanel;

    private JPanel transactionsPanel;

    private Account[] primaryAccounts;

    private Customer currentCustomer;

    public MonthlyUI() {
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

        wholePanel = new JPanel();

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
            currentCustomer = customerDao.queryCustomerByTax(usernameText.getText());
            if (currentCustomer != null) {
                // Check pin
                if (pinDao.verifyPin(usernameText.getText(), passwordText.getText())) {
                    String customerId = usernameText.getText();
                    this.removeAll();
                    // Primary account of current customer
                    primaryAccounts = accountDao.queryPrimaryAccountByCustomerId(customerId);
                    if (primaryAccounts == null || primaryAccounts.length == 0) {
                        JOptionPane.showMessageDialog(this, "You are not primary owner of any accounts!");
                        return;
                    }
                    createPrimaryArea();
                    createCoownerArea();
                    createTransactionArea();
                    wholePanel.setLayout(new GridLayout(3, 1));
                    this.add(wholePanel);
                    this.setLayout(new FlowLayout());
                    this.setVisible(true);
                    this.repaint();
                    this.validate();
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

    private void createPrimaryArea() {
        primaryPanel = new JPanel();
        primaryPanel.add(new JLabel("Primary Account:"));
        JPanel primary = new JPanel();
        primary.add(new JLabel("Account ID"));
        primary.add(new JLabel("Account Type"));
        primary.add(new JLabel("Initial Balance"));
        primary.add(new JLabel("Final Balance"));
        primary.add(new JLabel("If Insurance"));
        int line = 1;
        if (primaryAccounts != null && primaryAccounts.length != 0) {
            for (int i = 0; i < primaryAccounts.length; i++) {
                primary.add(new JLabel(String.valueOf(primaryAccounts[i].getId())));
                primary.add(new JLabel(String.valueOf(primaryAccounts[i].getType())));
                primary.add(new JLabel(calFirst(primaryAccounts[i]).setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
                primary.add(new JLabel(primaryAccounts[i].getBalance().setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
                if (primaryAccounts[i].getBalance().compareTo(new BigDecimal("100000")) > 0) {
                    primary.add(new JLabel("True"));
                } else {
                    primary.add(new JLabel("False"));
                }
                line++;
            }
        }
        primary.setLayout(new GridLayout(line, 5));
        primaryPanel.add(primary);
        //primaryPanel.setLayout(new GridLayout(2,1));
        //primaryPanel.setPreferredSize(new Dimension(800,400));
        wholePanel.add(primaryPanel);
    }

    private BigDecimal calFirst(Account account) {
        BigDecimal firstBalance = new BigDecimal(account.getBalance().toString());
        TransactionDao transactionDao = new TransactionDao();
        Transaction[] transactions = transactionDao.queryTransactionsByAccountId(account.getId());
        if (transactions != null && transactions.length != 0) {
            for (int i = transactions.length - 1; i >= 0 ; i--) {
                switch (transactions[i].getTransactionType()) {
                    case DEPOSIT:
                        firstBalance = firstBalance.subtract(transactions[i].getActual_money());
                        break;
                    case TRANSFER:
                        if (account.getId() == transactions[i].getFrom_id()) {
                            firstBalance = firstBalance.add(transactions[i].getActual_money());
                        } else {
                            firstBalance = firstBalance.subtract(transactions[i].getActual_money());
                        }
                        break;
                    case WIRE:
                        if (account.getId() == transactions[i].getFrom_id()) {
                            firstBalance = firstBalance.add(transactions[i].getMoney());
                        } else {
                            firstBalance = firstBalance.subtract(transactions[i].getActual_money());
                        }
                        break;
                    case WITHDRAWAL:
                        firstBalance = firstBalance.add(transactions[i].getActual_money());
                        break;
                    case ACCRUE_INTEREST:
                        firstBalance = firstBalance.subtract(transactions[i].getActual_money());
                        break;
                }
            }
        }
        return firstBalance;
    }


    private void createCoownerArea() {
        coownerPanel = new JPanel();
        coownerPanel.add(new JLabel("Co-owner Info:"));
        JPanel coowner = new JPanel();
        coowner.add(new JLabel("Account ID"));
        coowner.add(new JLabel("Customer Name"));
        coowner.add(new JLabel("Customer Address"));
        int line = 1;
        if (primaryAccounts != null && primaryAccounts.length != 0) {
            CustomerDao customerDao = new CustomerDao();
            for (int i = 0; i < primaryAccounts.length; i++) {
                Customer[] coowners = customerDao.queryCustomerByAccountId(String.valueOf(primaryAccounts[i].getId()));
                if (coowners != null && coowners.length != 0) {
                    for (int j = 0; j < coowners.length; j++) {
                        if (currentCustomer.getTax_id().equals(coowners[j].getTax_id())) {
                            continue;
                        }
                        coowner.add(new JLabel(String.valueOf(primaryAccounts[i].getId())));
                        coowner.add(new JLabel(coowners[j].getName()));
                        coowner.add(new JLabel(coowners[j].getAddress()));
                        line++;
                    }
                }

            }
        }
        coowner.setLayout(new GridLayout(line, 3));
        coownerPanel.add(coowner);
        //coownerPanel.setLayout(new GridLayout(2,1));
        //coownerPanel.setPreferredSize(new Dimension(800,400));
        wholePanel.add(coownerPanel);
    }

    private void createTransactionArea() {
        transactionsPanel = new JPanel();
        transactionsPanel.add(new JLabel("Transactions Info:"));
        JPanel trans = new JPanel();
        trans.add(new JLabel("Date"));
        trans.add(new JLabel("Type"));
        trans.add(new JLabel("Tax Id"));
        trans.add(new JLabel("From Id"));
        trans.add(new JLabel("To Id"));
        trans.add(new JLabel("Amount"));
        trans.add(new JLabel("Actual Amount"));
        trans.add(new JLabel("Fee"));
        trans.add(new JLabel("Check Number"));
        int line = 1;
        if (primaryAccounts != null && primaryAccounts.length != 0) {
            TransactionDao transactionDao = new TransactionDao();
            Transaction[] transactions = transactionDao.queryTransactionsByPrimaryOwner(currentCustomer.getTax_id());
            if (transactions != null && transactions.length != 0) {
                for (int i = 0; i < transactions.length; i++) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                    String date = sdf.format(transactions[i].getTran_date());
                    trans.add(new JLabel(date));
                    trans.add(new JLabel(transactions[i].getTransactionType().toString()));
                    trans.add(new JLabel(transactions[i].getCustomerId()));
                    trans.add(new JLabel(String.valueOf(transactions[i].getFrom_id())));
                    trans.add(new JLabel(String.valueOf(transactions[i].getTo_id())));
                    trans.add(new JLabel(transactions[i].getMoney().setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
                    trans.add(new JLabel(transactions[i].getActual_money().setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
                    trans.add(new JLabel(transactions[i].getFee().setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
                    trans.add(new JLabel(transactions[i].getCheck_number()));
                    line++;
                }
            }
        }
        trans.setLayout(new GridLayout(line, 9));
        transactionsPanel.add(trans);
        //transactionsPanel.setLayout(new GridLayout(2,1));
        //transactionsPanel.setPreferredSize(new Dimension(800,400));
        wholePanel.add(transactionsPanel);
    }

    public static void main(String[] args) {
        MonthlyUI monthlyUI = new MonthlyUI();
    }
}
