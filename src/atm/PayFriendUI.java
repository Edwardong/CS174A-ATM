package atm;

import Util.MoneyUtil;
import dao.AccountDao;
import dao.PocketDao;
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

public class PayFriendUI implements ActionListener {
    private JFrame payFriendFrame;

    private JPanel describePanel;

    private JPanel friendPanel;

    private JPanel amountPanel;

    private JPanel confirmPanel;

    private JTextField amountText;

    private JTextField friendText;

    private JButton confirmButton;

    private Account account;

    private String customerId;

    public PayFriendUI(Account account, String customerId){
        this.account = account;
        this.customerId = customerId;

        // Set the title of this frame
        payFriendFrame = new JFrame("Pay-Friend");

        // Initial describe area
        describePanel = new JPanel();
        describePanel.add(new JLabel("Please set amount and your friend`s account number!"));

        friendPanel = new JPanel();
        friendPanel.add(new JLabel("Account Id:"));
        friendText = new JTextField(20);
        friendPanel.add(friendText);


        // Initial input area
        amountPanel = new JPanel();
        amountPanel.add(new JLabel("Amount:"));
        amountText = new JTextField(20);
        amountPanel.add(amountText);

        // Initial confirm area
        confirmPanel = new JPanel();
        confirmButton = new JButton("Confirm");
        confirmPanel.add(confirmButton);

        payFriendFrame.add(describePanel);
        payFriendFrame.add(friendPanel);
        payFriendFrame.add(amountPanel);
        payFriendFrame.add(confirmPanel);

        payFriendFrame.setLayout(new FlowLayout());
        payFriendFrame.setVisible(true);
        payFriendFrame.setBounds(300, 300, 350, 400);

        confirmButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Confirm")){
            // Firstly verify money input is correct
            if(MoneyUtil.verifyMoney(amountText.getText())){
                // Get user`s input money
                BigDecimal current_money = new BigDecimal(amountText.getText());
                // Access database to get friend account info
                AccountDao accountDao = new AccountDao();
                int friend_id = Integer.valueOf(friendText.getText());
                BigDecimal friend_balance = accountDao.queryAccountById(friend_id).getBalance();
                // Judge if balance is enough
                if(account.getBalance().compareTo(current_money) < 0){
                    // Balance is not enough
                    JOptionPane.showMessageDialog(payFriendFrame, "Pay-Friend failed! Your pocket account does not have enough money!");
                }else{
                    // Update the amount of current account and link account
                    BigDecimal currentBalance = account.getBalance();
                    friend_balance = friend_balance.add(current_money);
                    currentBalance = currentBalance.subtract(current_money);
                    accountDao.updateBalance(account.getId(), currentBalance);
                    accountDao.updateBalance(friend_id, friend_balance);
                    account.setBalance(currentBalance);


                    // Finally create a new transaction
                    Transaction transaction = new Transaction();
                    // transaction.setTran_date(new Date());
                    transaction.setTran_date(TimeDao.getCurrentTime());
                    transaction.setTransactionType(TransactionType.PAY_FRIEND);
                    transaction.setCustomerId(customerId);
                    transaction.setFrom_id(account.getId());
                    transaction.setTo_id(friend_id);
                    transaction.setMoney(current_money);
                    transaction.setActual_money(current_money);
                    transaction.setFee(new BigDecimal("0"));
                    transaction.setCheck_number(null);

                    // Access to database and add a new transaction
                    TransactionDao transactionDao = new TransactionDao();
                    transactionDao.addTransaction(transaction);

                    // Finally check if link-account is closed
                    if(account.getBalance().compareTo(new BigDecimal("0.01")) < 0){
                        accountDao.updateState(account.getId(), true);
                        account.setClosed(true);
                    }

                    JOptionPane.showMessageDialog(payFriendFrame, "Pay-Friend succeed! Balance : $" + currentBalance.toString());
                    payFriendFrame.setVisible(false);
                }
            }
        }
    }
}
