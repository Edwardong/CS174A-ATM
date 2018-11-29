package bankteller;

import dao.AccountDao;
import dao.InterestDao;
import dao.TimeDao;
import dao.TransactionDao;
import model.Account;
import model.AccountType;
import model.Transaction;
import model.TransactionType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

public class AddInterestUI extends JPanel implements ActionListener {
    private JPanel interestShowPanel;

    private JPanel setInterestPanel;

    private JPanel addInterestPanel;

    private JButton setCheckButton;

    private JButton setSavingButton;

    private JButton addInterestButton;

    public AddInterestUI() {
        interestShowPanel = new JPanel();
        //interestShowPanel.add(new JLabel("Interest of Checking Account : " + InterestDao.getCheckingInterest() + "\r\n" + "Interest of Savings Account : " + InterestDao.getSavingsInterest()));

        setInterestPanel = new JPanel();
        setCheckButton = new JButton("Set Checking Account Interest");
        setSavingButton = new JButton("Set Savings Account Interest");
        setInterestPanel.add(setCheckButton);
        setInterestPanel.add(setSavingButton);

        addInterestPanel = new JPanel();
        addInterestButton = new JButton("Add Interest");
        addInterestPanel.add(addInterestButton);


        this.add(interestShowPanel);
        this.add(setInterestPanel);
        this.add(addInterestPanel);

        this.setLayout(new GridLayout(3, 1));
        this.setVisible(true);


        setCheckButton.addActionListener(this);
        setSavingButton.addActionListener(this);
        addInterestButton.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Set Checking Account Interest")) {
            BigDecimal newInte = new BigDecimal(JOptionPane.showInputDialog("Please input new interest(%)"));
            InterestDao.setCheckingInterest(newInte);
            JOptionPane.showMessageDialog(this, "Finished!");
        } else if (e.getActionCommand().equals("Set Savings Account Interest")) {
            BigDecimal newInte = new BigDecimal(JOptionPane.showInputDialog("Please input new interest(%)"));
            InterestDao.setSavingsInterest(newInte);
            JOptionPane.showMessageDialog(this, "Finished!");
        } else if (e.getActionCommand().equals("Add Interest")) {
            AccountDao accountDao = new AccountDao();
            Account[] accounts = accountDao.queryInterestAccount();
            if (accounts != null && accounts.length != 0) {
                for (int i = 0; i < accounts.length; i++) {
                    addInterest(accounts[i]);
                }
            }
            JOptionPane.showMessageDialog(this, "Finished!");
        }
    }

    private void addInterest(Account account) {
        BigDecimal inte;
        if(account.getType() == AccountType.INTEREST_CHECKING_ACCOUNT)
            inte = InterestDao.getCheckingInterest();
        else
            inte = InterestDao.getSavingsInterest();
        Date currentDate = TimeDao.getCurrentTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int days = lastDay - firstDay + 1;
        int currentDay = lastDay;

        BigDecimal finalBalance = account.getBalance();
        BigDecimal wholeBalance = new BigDecimal("0");
        BigDecimal interest = new BigDecimal("0");
        TransactionDao transactionDao = new TransactionDao();
        Transaction[] transactions = transactionDao.queryTransactionsDuringCurrentMonthWithAccountId(account.getId());
        if (transactions != null && transactions.length != 0) {
            for (int i = transactions.length - 1; i >= 0; i--) {
                switch (transactions[i].getTransactionType()) {
                    case DEPOSIT:
                        Calendar temp = Calendar.getInstance();
                        temp.setTime(transactions[i].getTran_date());
                        int tranDay = temp.get(Calendar.DATE);
                        int interval = currentDay - tranDay;
                        currentDay = tranDay;
                        wholeBalance = wholeBalance.add(finalBalance.multiply(new BigDecimal(interval)));
                        // interest = interest.add(finalBalance.multiply(new BigDecimal(interval)).multiply(inte).divide(new BigDecimal("100")));
                        finalBalance = finalBalance.subtract(transactions[i].getActual_money());
                        break;
                    case TRANSFER:
                        temp = Calendar.getInstance();
                        temp.setTime(transactions[i].getTran_date());
                        tranDay = temp.get(Calendar.DATE);
                        interval = currentDay - tranDay;
                        currentDay = tranDay;
                        wholeBalance = wholeBalance.add(finalBalance.multiply(new BigDecimal(interval)));
                        //interest = interest.add(finalBalance.multiply(new BigDecimal(interval)).multiply(inte).divide(new BigDecimal("100")));
                        if (account.getId() == transactions[i].getFrom_id()) {
                            finalBalance = finalBalance.add(transactions[i].getActual_money());
                        } else {
                            finalBalance = finalBalance.subtract(transactions[i].getActual_money());
                        }
                        break;
                    case WIRE:
                        temp = Calendar.getInstance();
                        temp.setTime(transactions[i].getTran_date());
                        tranDay = temp.get(Calendar.DATE);
                        interval = currentDay - tranDay;
                        currentDay = tranDay;
                        wholeBalance = wholeBalance.add(finalBalance.multiply(new BigDecimal(interval)));
                        //interest = interest.add(finalBalance.multiply(new BigDecimal(interval)).multiply(inte).divide(new BigDecimal("100")));
                        if (account.getId() == transactions[i].getFrom_id()) {
                            finalBalance = finalBalance.add(transactions[i].getMoney());
                        } else {
                            finalBalance = finalBalance.subtract(transactions[i].getActual_money());
                        }
                        break;
                    case WITHDRAWAL:
                        temp = Calendar.getInstance();
                        temp.setTime(transactions[i].getTran_date());
                        tranDay = temp.get(Calendar.DATE);
                        interval = currentDay - tranDay;
                        currentDay = tranDay;
                        wholeBalance = wholeBalance.add(finalBalance.multiply(new BigDecimal(interval)));
                        //interest = interest.add(finalBalance.multiply(new BigDecimal(interval)).multiply(inte).divide(new BigDecimal("100")));
                        finalBalance = finalBalance.add(transactions[i].getActual_money());
                        break;
                }
            }
        }
        int interval = currentDay - firstDay;
        wholeBalance = wholeBalance.add(finalBalance.multiply(new BigDecimal(interval))).add(account.getBalance());
        interest = wholeBalance.divide(new BigDecimal(days),10 ,ROUND_HALF_DOWN).multiply(inte).divide(new BigDecimal("100"));
        //interest = interest.add(finalBalance.multiply(new BigDecimal(interval)).multiply(inte).divide(new BigDecimal("100")));

        //interest = interest.divide(new BigDecimal(days));

        AccountDao accountDao = new AccountDao();
        accountDao.updateBalance(account.getId(), account.getBalance().add(interest));

        Transaction transaction = new Transaction();
        // transaction.setTran_date(new Date());
        transaction.setTran_date(TimeDao.getCurrentTime());
        transaction.setTransactionType(TransactionType.ACCRUE_INTEREST);
        transaction.setCustomerId(account.getPrimary_owner());
        transaction.setFrom_id(account.getId());
        transaction.setTo_id(account.getId());
        transaction.setMoney(interest);
        transaction.setActual_money(interest);
        transaction.setFee(new BigDecimal("0"));
        transaction.setCheck_number(null);

        // Access to database and add a new transaction
        transactionDao.addTransaction(transaction);
    }
}
