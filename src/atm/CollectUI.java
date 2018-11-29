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

public class CollectUI implements ActionListener {
    private JFrame collectFrame;

    private JPanel describePanel;

    private JPanel amountPanel;

    private JPanel confirmPanel;

    private JTextField amountText;

    private JButton confirmButton;

    private Account account;

    private String customerId;

    public CollectUI(Account account, String customerId){
        this.account = account;
        this.customerId = customerId;

        // Set the title of this frame
        collectFrame = new JFrame("Collect");

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

        collectFrame.add(describePanel);
        collectFrame.add(amountPanel);
        collectFrame.add(confirmPanel);

        collectFrame.setLayout(new FlowLayout());
        collectFrame.setVisible(true);
        collectFrame.setBounds(300, 300, 350, 400);

        confirmButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Confirm")){
            // Firstly verify money input is correct
            if(MoneyUtil.verifyMoney(amountText.getText())){
                // Get user`s input money
                BigDecimal current_money = new BigDecimal(amountText.getText());
                // Access database to get link-account info
                PocketDao pocketDao = new PocketDao();
                AccountDao accountDao = new AccountDao();
                int link_account_id = pocketDao.queryAsscAccountByPocketAccount(account.getId());
                BigDecimal link_account_balance = accountDao.queryAccountById(link_account_id).getBalance();
                // Judge if link account is closed
                if (accountDao.queryAccountById(link_account_id).getClosed()){
                    JOptionPane.showMessageDialog(collectFrame, "Topup failed! Your linked account is closed!");
                }
                // Judge if balance is enough
                else if(account.getBalance().compareTo(current_money) < 0){
                    // Balance is not enough
                    JOptionPane.showMessageDialog(collectFrame, "Collect failed! Your pocket account does not have enough money!");
                }else{
                    // Update the amount of current account and link account
                    BigDecimal currentBalance = account.getBalance();
                    currentBalance = currentBalance.subtract(current_money);
                    link_account_balance = link_account_balance.add(current_money.multiply(new BigDecimal("0.97")));

                    pocketDao.updateBalance(account.getId(), currentBalance);
                    accountDao.updateBalance(link_account_id, link_account_balance);
                    account.setBalance(currentBalance);


                    // Finally create a new transaction
                    Transaction transaction = new Transaction();
                    // transaction.setTran_date(new Date());
                    transaction.setTran_date(TimeDao.getCurrentTime());
                    transaction.setTransactionType(TransactionType.COLLECT);
                    transaction.setCustomerId(customerId);
                    transaction.setFrom_id(account.getId());
                    transaction.setTo_id(link_account_id);
                    transaction.setMoney(current_money);
                    transaction.setActual_money(current_money.multiply(new BigDecimal("0.97")));
                    transaction.setFee(current_money.multiply(new BigDecimal("0.03")));
                    transaction.setCheck_number(null);

                    // Access to database and add a new transaction
                    TransactionDao transactionDao = new TransactionDao();
                    transactionDao.addTransaction(transaction);

                    // Finally check if link-account is closed
                    if(currentBalance.compareTo(new BigDecimal("0.01")) < 0){
                        accountDao.updateState(account.getId(), true);
                        account.setClosed(true);
                    }

                    JOptionPane.showMessageDialog(collectFrame, "Collect succeed! Current Pocket Account`s Balance : $" + currentBalance.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                    collectFrame.setVisible(false);
                }
            }
        }
    }
}
