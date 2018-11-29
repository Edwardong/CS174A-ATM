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

public class DepositUI implements ActionListener {
    private JFrame depositFrame;

    private JPanel describePanel;

    private JPanel amountPanel;

    private JPanel confirmPanel;

    private JTextField amountText;

    private JButton confirmButton;

    private Account account;

    private String customerId;

    public DepositUI(Account account, String customerId){
        this.account = account;
        this.customerId = customerId;

        // Set the title of this frame
        depositFrame = new JFrame("Deposit");
        // depositFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Initial describe area
        describePanel = new JPanel();
        describePanel.add(new JLabel("Please insert your money!"));

        // Initial input area
        amountPanel = new JPanel();
        amountPanel.add(new JLabel("Amount:"));
        amountText = new JTextField(20);
        amountPanel.add(amountText);

        // Initial confirm area
        confirmPanel = new JPanel();
        confirmButton = new JButton("Confirm");
        confirmPanel.add(confirmButton);

        depositFrame.add(describePanel);
        depositFrame.add(amountPanel);
        depositFrame.add(confirmPanel);

        depositFrame.setLayout(new FlowLayout());
        depositFrame.setVisible(true);
        depositFrame.setBounds(300, 300, 350, 400);

        confirmButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Confirm")){
            // Firstly verify money input is correct
            if(MoneyUtil.verifyMoney(amountText.getText())){
                // Firstly create a transaction object
                BigDecimal money = new BigDecimal(amountText.getText());

                // Update the amount of current account
                // Current money
                BigDecimal currentBalance = account.getBalance();
                currentBalance = currentBalance.add(money);
                AccountDao accountDao = new AccountDao();
                accountDao.updateBalance(account.getId(), currentBalance);
                account.setBalance(currentBalance);

                Transaction transaction = new Transaction();
                // transaction.setTran_date(new Date());
                transaction.setTran_date(TimeDao.getCurrentTime());
                transaction.setTransactionType(TransactionType.DEPOSIT);
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

                JOptionPane.showMessageDialog(depositFrame, "Deposit succeed! Balance : $" + currentBalance.toString());
                depositFrame.setVisible(false);
            }
            else{
                JOptionPane.showMessageDialog(depositFrame, "The format of money is wrong!");
            }
        }
    }


}
