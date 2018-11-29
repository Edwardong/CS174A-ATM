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

public class WireUI implements ActionListener {
    private JFrame wireFrame;

    private JPanel describePanel;

    private JPanel transferPanel;

    private JPanel amountPanel;

    private JPanel confirmPanel;

    private JTextField amountText;

    private JTextField transferText;

    private JButton confirmButton;

    private Account account;

    private String customerId;

    public WireUI(Account account, String customerId){
        this.account = account;
        this.customerId = customerId;

        // Set the title of this frame
        wireFrame = new JFrame("Wire");

        // Initial describe area
        describePanel = new JPanel();
        describePanel.add(new JLabel("Please set amount and account number!"));

        transferPanel = new JPanel();
        transferPanel.add(new JLabel("Account Id:"));
        transferText = new JTextField(20);
        transferPanel.add(transferText);


        // Initial input area
        amountPanel = new JPanel();
        amountPanel.add(new JLabel("Amount:"));
        amountText = new JTextField(20);
        amountPanel.add(amountText);

        // Initial confirm area
        confirmPanel = new JPanel();
        confirmButton = new JButton("Confirm");
        confirmPanel.add(confirmButton);

        wireFrame.add(describePanel);
        wireFrame.add(transferPanel);
        wireFrame.add(amountPanel);
        wireFrame.add(confirmPanel);

        wireFrame.setLayout(new FlowLayout());
        wireFrame.setVisible(true);
        wireFrame.setBounds(300, 300, 350, 400);

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
                int transfer_id = Integer.valueOf(transferText.getText());
                if(accountDao.queryAccountById(transfer_id) == null){
                    JOptionPane.showMessageDialog(wireFrame, "Account dose not exist!");
                    return;
                }
                BigDecimal transfer_balance = accountDao.queryAccountById(transfer_id).getBalance();
                // Judge if balance is enough
                if(account.getBalance().compareTo(current_money) < 0){
                    // Balance is not enough
                    JOptionPane.showMessageDialog(wireFrame, "Wire failed! Your account does not have enough money!");
                }else{
                    // Update the amount of current account and link account
                    BigDecimal currentBalance = account.getBalance();
                    transfer_balance = transfer_balance.add(current_money.multiply(new BigDecimal("0.98")));
                    currentBalance = currentBalance.subtract(current_money);
                    accountDao.updateBalance(account.getId(), currentBalance);
                    accountDao.updateBalance(transfer_id, transfer_balance);
                    account.setBalance(currentBalance);


                    // Finally create a new transaction
                    Transaction transaction = new Transaction();
                    // transaction.setTran_date(new Date());
                    transaction.setTran_date(TimeDao.getCurrentTime());
                    transaction.setTransactionType(TransactionType.WIRE);
                    transaction.setCustomerId(customerId);
                    transaction.setFrom_id(account.getId());
                    transaction.setTo_id(transfer_id);
                    transaction.setMoney(current_money);
                    transaction.setActual_money(current_money.multiply(new BigDecimal("0.98")));
                    transaction.setFee(current_money.multiply(new BigDecimal("0.02")));
                    transaction.setCheck_number(null);

                    // Access to database and add a new transaction
                    TransactionDao transactionDao = new TransactionDao();
                    transactionDao.addTransaction(transaction);

                    // Finally check if link-account is closed
                    if(account.getBalance().compareTo(new BigDecimal("0.01")) < 0){
                        accountDao.updateState(account.getId(), true);
                        account.setClosed(true);
                    }

                    JOptionPane.showMessageDialog(wireFrame, "Wire succeed! Balance : $" + currentBalance.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                    wireFrame.setVisible(false);
                }
            }
        }
    }
}
