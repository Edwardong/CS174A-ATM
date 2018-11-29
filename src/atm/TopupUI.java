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

public class TopupUI implements ActionListener {
    private JFrame topupFrame;

    private JPanel describePanel;

    private JPanel amountPanel;

    private JPanel confirmPanel;

    private JTextField amountText;

    private JButton confirmButton;

    private Account account;

    private String customerId;

    public TopupUI(Account account, String customerId){
        this.account = account;
        this.customerId = customerId;

        // Set the title of this frame
        topupFrame = new JFrame("Top-up");

        // Initial describe area
        describePanel = new JPanel();
        describePanel.add(new JLabel("Please set top-up money!"));

        // Initial input area
        amountPanel = new JPanel();
        amountPanel.add(new JLabel("Amount:"));
        amountText = new JTextField(20);
        amountPanel.add(amountText);

        // Initial confirm area
        confirmPanel = new JPanel();
        confirmButton = new JButton("Confirm");
        confirmPanel.add(confirmButton);

        topupFrame.add(describePanel);
        topupFrame.add(amountPanel);
        topupFrame.add(confirmPanel);

        topupFrame.setLayout(new FlowLayout());
        topupFrame.setVisible(true);
        topupFrame.setBounds(300, 300, 350, 400);

        confirmButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Confirm")){
            // Firstly verify money input is correct
            if(MoneyUtil.verifyMoney(amountText.getText())){
                // Get user`s input money
                BigDecimal current_topup = new BigDecimal(amountText.getText());
                // Access database to get link-account info
                PocketDao pocketDao = new PocketDao();
                AccountDao accountDao = new AccountDao();
                int link_account_id = pocketDao.queryAsscAccountByPocketAccount(account.getId());
                BigDecimal link_account_balance = accountDao.queryAccountById(link_account_id).getBalance();
                // Judge if link account is closed
                if (accountDao.queryAccountById(link_account_id).getClosed()){
                    JOptionPane.showMessageDialog(topupFrame, "Topup failed! Your linked account is closed!");
                }
                // Judge if balance is enough
                else if(link_account_balance.compareTo(current_topup) < 0){
                    // Balance is not enough
                    JOptionPane.showMessageDialog(topupFrame, "Topup failed! Your linked account does not have enough money!");
                }else{
                    // Update the amount of current account and link account
                    BigDecimal currentBalance = account.getBalance();
                    link_account_balance = link_account_balance.subtract(current_topup);
                    currentBalance = currentBalance.add(current_topup);
                    pocketDao.updateBalance(account.getId(), currentBalance);
                    accountDao.updateBalance(link_account_id, link_account_balance);
                    account.setBalance(currentBalance);


                    // Finally create a new transaction
                    Transaction transaction = new Transaction();
                    // transaction.setTran_date(new Date());
                    transaction.setTran_date(TimeDao.getCurrentTime());
                    transaction.setTransactionType(TransactionType.TOP_UP);
                    transaction.setCustomerId(customerId);
                    transaction.setFrom_id(link_account_id);
                    transaction.setTo_id(account.getId());
                    transaction.setMoney(current_topup);
                    transaction.setActual_money(current_topup);
                    transaction.setFee(new BigDecimal("0"));
                    transaction.setCheck_number(null);

                    // Access to database and add a new transaction
                    TransactionDao transactionDao = new TransactionDao();
                    transactionDao.addTransaction(transaction);

                    // Finally check if link-account is closed
                    if(link_account_balance.compareTo(new BigDecimal("0.01")) < 0){
                        accountDao.updateState(link_account_id, true);
                        account.setClosed(true);
                    }

                    JOptionPane.showMessageDialog(topupFrame, "Top-up succeed! Balance : $" + currentBalance.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                    topupFrame.setVisible(false);
                }
            }
        }
    }
}
