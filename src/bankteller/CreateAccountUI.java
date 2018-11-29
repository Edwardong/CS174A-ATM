package bankteller;

import atm.DepositUI;
import atm.TopupUI;
import dao.*;
import jdk.nashorn.internal.scripts.JO;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class CreateAccountUI extends JPanel implements ActionListener {
    private JPanel customerPanel;

    private JPanel accountPanel;

    private JPanel accountTypePanel;

    private JPanel linkAccountPanel;

    private JPanel bankBranchPanel;

    private JPanel moneyPanel;

    private JTextField customerField;

    private JTextField accountField;

    private JComboBox typeField;

    private JTextField linkAccountField;

    private JTextField bankBranchField;

    private JTextField moneyField;

    private JPanel confirmPanel;

    private JButton confirmButton;

    private int ifPrimary;

    private int ifPocket;

    public CreateAccountUI() {
        // Choose to be a primary owner or co-owner
        ifPrimary = JOptionPane.showConfirmDialog(this, "Click yes if you want to be a primary owner of a new account.\r\nClick no if you want to be a co-owner of an existed account");
        ifPocket = 1;
        if (ifPrimary == 0) {
            // If create a pocket account
            ifPocket = JOptionPane.showConfirmDialog(this, "Do you want to create a pocket account?");
        }

        if (ifPocket == 2 || ifPrimary == 2)
            return;

        customerPanel = new JPanel();
        customerPanel.add(new JLabel("Tax identification number"));
        customerField = new JTextField(20);
        customerPanel.add(customerField);

        accountPanel = new JPanel();
        accountPanel.add(new JLabel("Account id"));
        accountField = new JTextField(20);
        accountPanel.add(accountField);

        accountTypePanel = new JPanel();
        String[] choice = {"STUDENT_CHECKING_ACCOUNT", "INTEREST_CHECKING_ACCOUNT", "SAVING_ACCOUNT"};
        accountTypePanel.add(new JLabel("Account Type"));
        typeField = new JComboBox(choice);
        accountTypePanel.add(typeField);

        linkAccountPanel = new JPanel();
        linkAccountPanel.add(new JLabel("Linked Account id"));
        linkAccountField = new JTextField(20);
        linkAccountPanel.add(linkAccountField);

        bankBranchPanel = new JPanel();
        bankBranchPanel.add(new JLabel("Bank Branch Name"));
        bankBranchField = new JTextField(20);
        bankBranchPanel.add(bankBranchField);

        moneyPanel = new JPanel();
        moneyPanel.add(new JLabel("Money"));
        moneyField = new JTextField(20);
        moneyPanel.add(moneyField);

        confirmPanel = new JPanel();
        confirmButton = new JButton("Confirm");
        confirmPanel.add(confirmButton);

        int line = 3;

        // If create with primary account
        if (ifPrimary == 0) {
            this.add(customerPanel);
            this.add(accountPanel);
            // If create a pocket account
            if (ifPocket == 0) {
                this.add(linkAccountPanel);
            } else {
                this.add(accountTypePanel);
            }
            this.add(bankBranchPanel);
            this.add(confirmPanel);
            // this.add(moneyPanel);
            line = 6;
        } else if (ifPrimary == 1) {
            this.add(customerPanel);
            this.add(accountPanel);
            this.add(confirmPanel);
        }

        this.setLayout(new GridLayout(line, 1));
        confirmButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Confirm")){
            if (ifPrimary == 0) {
                CustomerDao customerDao = new CustomerDao();
                String customerId = customerField.getText();
                Customer customer = customerDao.queryCustomerByTax(customerId);
                // Customer dose not exist
                if (customer == null) {
                    String name = JOptionPane.showInputDialog("You are new in our system. Please input your name");
                    String address = JOptionPane.showInputDialog("You are new in our system. Please input your address");
                    String pin = JOptionPane.showInputDialog("Reset your PIN");
                    customer = new Customer(customerId, name, address);
                    customerDao.addCustomer(customer);
                    // Reset Pin
                    PinDao pinDao = new PinDao();
                    pinDao.setPin(customerId, "1717", pin);
                }
                String id = accountField.getText();
                AccountType type = null;
                if (ifPocket == 0) {
                    type = AccountType.POCKET_ACCOUNT;
                } else {
                    switch (typeField.getSelectedIndex()) {
                        case 0:
                            type = AccountType.STUDENT_CHECKING_ACCOUNT;
                            break;
                        case 1:
                            type = AccountType.INTEREST_CHECKING_ACCOUNT;
                            break;
                        case 2:
                            type = AccountType.SAVING_ACCOUNT;
                    }
                }
                String primary_owner = customerId;
                String bank_branch_name = bankBranchField.getText();
                // BigDecimal balance = new BigDecimal(moneyField.getText());
                BigDecimal interest_rate = null;
                switch (type) {
                    case STUDENT_CHECKING_ACCOUNT:
                        interest_rate = new BigDecimal("0.0");
                        break;
                    case INTEREST_CHECKING_ACCOUNT:
                        interest_rate = new BigDecimal("5.5");
                        break;
                    case SAVING_ACCOUNT:
                        interest_rate = new BigDecimal("7.5");
                        break;
                    case POCKET_ACCOUNT:
                        interest_rate = new BigDecimal("0.0");
                        break;
                }
                boolean closed = false;
                Account account = new Account(Integer.valueOf(id), type, primary_owner, bank_branch_name, new BigDecimal("0"), interest_rate, closed);
                AccountDao accountDao = new AccountDao();
                if (accountDao.queryAccountById(account.getId()) != null) {
                    JOptionPane.showMessageDialog(this, "Repeated Account Id!");
                    return;
                }
                // Judge if current customer has checking/savings account
                if (ifPocket == 0) {
                    Account[] accounts = accountDao.queryAccountByCustomerId(customerId);
                    if (accounts == null || accounts.length == 0) {
                        JOptionPane.showMessageDialog(this, "You have no checking/savings account!");
                        return;
                    }
                    // Judge if link account id belongs to current customer
                    boolean ifFind = false;
                    for (int i = 0; i < accounts.length; i++) {
                        if (accounts[i].getId() == Integer.valueOf(linkAccountField.getText()) && accounts[i].getType() != AccountType.POCKET_ACCOUNT && !accounts[i].getClosed()) {
                            ifFind = true;
                            break;
                        }
                    }
                    if (!ifFind) {
                        JOptionPane.showMessageDialog(this, "Linked account is invalid!");
                        return;
                    }
                }
                // Create a account
                accountDao.addAcount(account);
                // Add customer and account association
                CustAccoDao custAccoDao = new CustAccoDao();
                custAccoDao.addRelation(customerId, Integer.valueOf(id));
                // If pocket account create a association with linked-account
//                if (ifPocket == 0) {
//                    PocketDao pocketDao = new PocketDao();
//                    pocketDao.addAssociation(Integer.valueOf(id), Integer.valueOf(linkAccountField.getText()));
//                    TopupUI topupUI = new TopupUI(account, customerId);
//                } else {
//                    DepositUI depositUI = new DepositUI(account, customerId);
//                }

                JOptionPane.showMessageDialog(this,"Primary account created successfully!");

                while (JOptionPane.showConfirmDialog(this, "Do you want to create a Co-owner Account?") == 0){
                    String tax_id = JOptionPane.showInputDialog("Please input your tax id");
                    customer = customerDao.queryCustomerByTax(tax_id);
                    if (customer == null) {
                        String name = JOptionPane.showInputDialog("You are new in our system. Please input your name");
                        String address = JOptionPane.showInputDialog("You are new in our system. Please input your address");
                        String pin = JOptionPane.showInputDialog("Reset your pin");
                        customer = new Customer(tax_id, name, address);
                        customerDao.addCustomer(customer);
                        // Reset Pin
                        PinDao pinDao = new PinDao();
                        pinDao.setPin(tax_id, "1717", pin);
                    }
                    custAccoDao.addRelation(tax_id, Integer.valueOf(account.getId()));
                    JOptionPane.showMessageDialog(this,"Co-owner account set up successfully!");
                }

                String tax_id = JOptionPane.showInputDialog("Please choose a customer to initial the account");
                if (ifPocket == 0) {
                    PocketDao pocketDao = new PocketDao();
                    pocketDao.addAssociation(Integer.valueOf(id), Integer.valueOf(linkAccountField.getText()));
                    TopupUI topupUI = new TopupUI(account, tax_id);
                } else {
                    DepositUI depositUI = new DepositUI(account, tax_id);
                }

                this.setVisible(false);
                this.repaint();
                this.revalidate();

            } else {
                AccountDao accountDao = new AccountDao();
                Account account = accountDao.queryAccountById(Integer.valueOf(accountField.getText()));
                // Customer dose not exist
                if (account == null) {
                    JOptionPane.showMessageDialog(this, "No such account!");
                    this.setVisible(false);
                    this.repaint();
                    this.revalidate();
                    return;
                } else {
                    CustomerDao customerDao = new CustomerDao();
                    String customerId = customerField.getText();
                    Customer customer = customerDao.queryCustomerByTax(customerId);
                    if (customer == null) {
                        String name = JOptionPane.showInputDialog("You are new in our system. Please input your name");
                        String address = JOptionPane.showInputDialog("You are new in our system. Please input your address");
                        String pin = JOptionPane.showInputDialog("Reset your pin");
                        customer = new Customer(customerId, name, address);
                        customerDao.addCustomer(customer);
                        // Reset Pin
                        PinDao pinDao = new PinDao();
                        pinDao.setPin(customerId, "1717", pin);
                    }
                    CustAccoDao custAccoDao = new CustAccoDao();
                    custAccoDao.addRelation(customerId, Integer.valueOf(account.getId()));
                    JOptionPane.showMessageDialog(this,"Co-owner account set up successfully!");
                }
            }
        }
    }
}