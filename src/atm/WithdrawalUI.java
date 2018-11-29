package atm;

import Util.MoneyUtil;
import dao.AccountDao;
import dao.TimeDao;
import dao.TransactionDao;
import model.Account;
import model.Customer;
import model.Transaction;
import model.TransactionType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Date;

public class WithdrawalUI implements ActionListener {
    private JFrame withdrawalFrame;

    private JPanel describePanel;

    private JPanel amountPanel;

    private JPanel confirmPanel;

    private JTextField amountText;

    private JButton confirmButton;

    private Account account;

    private String customerId;

    public WithdrawalUI(Account account, String customerId){
        this.account = account;
        this.customerId = customerId;

        // Set the title of this frame
        withdrawalFrame = new JFrame("Withdrawal");
        // depositFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Initial describe area
        describePanel = new JPanel();
        describePanel.add(new JLabel("Please set amount!"));

        // Initial input area
        amountPanel = new JPanel();
        amountPanel.add(new JLabel("Amount:"));
        amountText = new JTextField(20);
        amountPanel.add(amountText);

        // Initial confirm area
        confirmPanel = new JPanel();
        confirmButton = new JButton("Confirm");
        confirmPanel.add(confirmButton);

        withdrawalFrame.add(describePanel);
        withdrawalFrame.add(amountPanel);
        withdrawalFrame.add(confirmPanel);

        withdrawalFrame.setLayout(new FlowLayout());
        withdrawalFrame.setVisible(true);
        withdrawalFrame.setBounds(300, 300, 350, 400);

        confirmButton.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Confirm")) {
            // Firstly verify money input is correct
            if (MoneyUtil.verifyMoney(amountText.getText())) {
                // Firstly create a transaction object
                BigDecimal money = new BigDecimal(amountText.getText());

                // Check if the balance is enough
                if(money.compareTo(account.getBalance()) <= 0){
                    BigDecimal currentBalance = account.getBalance();

                    // Update the amount of current account
                    currentBalance = currentBalance.subtract(money);
                    AccountDao accountDao = new AccountDao();
                    accountDao.updateBalance(account.getId(), currentBalance);
                    account.setBalance(currentBalance);

                    Transaction transaction = new Transaction();
                    // transaction.setTran_date(new Date());
                    transaction.setTran_date(TimeDao.getCurrentTime());
                    transaction.setTransactionType(TransactionType.WITHDRAWAL);
                    transaction.setCustomerId(customerId);
                    transaction.setFrom_id(account.getId());
                    transaction.setTo_id(account.getId());
                    transaction.setMoney(money);
                    transaction.setActual_money(money);
                    transaction.setFee(new BigDecimal("0"));
                    transaction.setCheck_number(null);

                    // Access to database and add a new transaction
                    TransactionDao transactionDao = new TransactionDao();
                    transactionDao.addTransaction(transaction);

                    // Finally check if current account is closed
                    if(currentBalance.compareTo(new BigDecimal("0.01")) < 0){
                        accountDao.updateState(account.getId(), true);
                        account.setClosed(true);
                    }

                    JOptionPane.showMessageDialog(withdrawalFrame, "Withdrawal succeed! Balance : $" + currentBalance.toString());
                    withdrawalFrame.setVisible(false);
                }else{
                    JOptionPane.showMessageDialog(withdrawalFrame, "Your balance is not enough! Balance : $" + account.getBalance().toString());
                }
            } else {
                JOptionPane.showMessageDialog(withdrawalFrame, "The format of money is wrong!");
            }
        }
    }
}
