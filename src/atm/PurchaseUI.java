package atm;

import Util.MoneyUtil;
import dao.AccountDao;
import dao.TimeDao;
import dao.TransactionDao;
import model.Account;
import model.Transaction;
import model.TransactionType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Date;

public class PurchaseUI implements ActionListener {
    private JFrame purchaseFrame;

    private JPanel describePanel;

    private JPanel amountPanel;

    private JPanel confirmPanel;

    private JTextField amountText;

    private JButton confirmButton;

    private Account account;

    private String customerId;

    public PurchaseUI(Account account, String customerId){
        this.account = account;
        this.customerId = customerId;

        // Set the title of this frame
        purchaseFrame = new JFrame("Purchase");

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

        purchaseFrame.add(describePanel);
        purchaseFrame.add(amountPanel);
        purchaseFrame.add(confirmPanel);

        purchaseFrame.setLayout(new FlowLayout());
        purchaseFrame.setVisible(true);
        purchaseFrame.setBounds(300, 300, 350, 400);

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
                    transaction.setTransactionType(TransactionType.PURCHASE);
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

                    // Finally check if link-account is closed
                    if(currentBalance.compareTo(new BigDecimal("0.01")) < 0){
                        accountDao.updateState(account.getId(), true);
                        account.setClosed(true);
                    }

                    JOptionPane.showMessageDialog(purchaseFrame, "Withdrawal succeed! Balance : $" + currentBalance.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                    purchaseFrame.setVisible(false);
                }else{
                    JOptionPane.showMessageDialog(purchaseFrame, "Your balance is not enough! Balance : $" + account.getBalance().setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                }
            } else {
                JOptionPane.showMessageDialog(purchaseFrame, "The format of money is wrong!");
            }
        }
    }
}
