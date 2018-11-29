package atm;

import dao.AccountDao;
import model.Account;
import model.AccountType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuUI implements ActionListener {
    private JFrame menuFrame;

    private JPanel menuPanel;

    private JButton depositButton;

    private JButton topupButton;

    private JButton withdrawalButton;

    private JButton purchaseButton;

    private JButton transferButton;

    private JButton collectButton;

    private JButton wireButton;

    private JButton payFriendButton;

    private JButton backButton;

    private Account account;

    private AccountDao accountDao;

    private String customerId;

    public MenuUI(String customerId) {
        this.customerId = customerId;

        // Set the title of this frame
        menuFrame = new JFrame("ATM-App");
        menuFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Initial all button
        depositButton = new JButton("Deposit");
        topupButton = new JButton("Top-up");
        withdrawalButton = new JButton("Withdrawal");
        purchaseButton = new JButton("Purchase");
        transferButton = new JButton("Transfer");
        collectButton = new JButton("Collect");
        wireButton = new JButton("Wire");
        payFriendButton = new JButton("Pay-friend");
        backButton = new JButton("Logout");

        // Ues panel to store all buttons
        menuPanel = new JPanel();
        menuPanel.add(depositButton);
        menuPanel.add(topupButton);
        menuPanel.add(withdrawalButton);
        menuPanel.add(purchaseButton);
        menuPanel.add(transferButton);
        menuPanel.add(collectButton);
        menuPanel.add(wireButton);
        menuPanel.add(payFriendButton);
        menuPanel.add(backButton);

        // Set layout
        menuPanel.setLayout(new GridLayout(5, 2, 20, 20));

        menuFrame.add(menuPanel);
        menuFrame.setVisible(true);
        menuFrame.setLayout(new FlowLayout());
        menuFrame.setBounds(300, 300, 350, 400);

        // Add listener
        depositButton.addActionListener(this);
        topupButton.addActionListener(this);
        withdrawalButton.addActionListener(this);
        purchaseButton.addActionListener(this);
        transferButton.addActionListener(this);
        collectButton.addActionListener(this);
        wireButton.addActionListener(this);
        payFriendButton.addActionListener(this);
        backButton.addActionListener(this);

        accountDao = new AccountDao();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Logout")) {
            menuFrame.setVisible(false);
            LoginUI newLogin = new LoginUI();
        } else if (e.getActionCommand().equals("Deposit")) {
            String accountid = JOptionPane.showInputDialog(new AccountSelectUI(customerId, false));
            if(accountid == null)return;
            this.account = accountDao.queryAccountById(Integer.valueOf(accountid));
            // If current account is not closed
            if (!account.getClosed()) {
                // Open deposit ui
                DepositUI depositUI = new DepositUI(account, customerId);
            } else {
                JOptionPane.showMessageDialog(menuFrame, "Current account is closed!");
            }

        } else if (e.getActionCommand().equals("Top-up")) {
            String accountid = JOptionPane.showInputDialog(new AccountSelectUI(customerId, true));
            if(accountid == null)return;
            this.account = accountDao.queryAccountById(Integer.valueOf(accountid));
            // If current account is not closed
            if (!account.getClosed()) {
                // Open top-up ui
                TopupUI topupUI = new TopupUI(account, customerId);
            } else {
                JOptionPane.showMessageDialog(menuFrame, "Current account is closed!");
            }
        } else if (e.getActionCommand().equals("Withdrawal")) {
            String accountid = JOptionPane.showInputDialog(new AccountSelectUI(customerId, false));
            if(accountid == null)return;
            this.account = accountDao.queryAccountById(Integer.valueOf(accountid));
            // If current account is not closed
            if (!account.getClosed()) {
                // Open Withdrawal ui
                WithdrawalUI withdrawalUI = new WithdrawalUI(account, customerId);
            } else {
                JOptionPane.showMessageDialog(menuFrame, "Current account is closed!");
            }
        } else if (e.getActionCommand().equals("Purchase")) {
            String accountid = JOptionPane.showInputDialog(new AccountSelectUI(customerId, true));
            if(accountid == null)return;
            this.account = accountDao.queryAccountById(Integer.valueOf(accountid));
            // If current account is not closed
            if (!account.getClosed()) {
                // Open Purchase ui
                PurchaseUI purchaseUI = new PurchaseUI(account, customerId);
            } else {
                JOptionPane.showMessageDialog(menuFrame, "Current account is closed!");
            }
        } else if (e.getActionCommand().equals("Collect")) {
            String accountid = JOptionPane.showInputDialog(new AccountSelectUI(customerId, true));
            if(accountid == null)return;
            this.account = accountDao.queryAccountById(Integer.valueOf(accountid));
            // If current account is not closed
            if (!account.getClosed()) {
                // Open collect ui
                CollectUI collectUI = new CollectUI(account, customerId);
            } else {
                JOptionPane.showMessageDialog(menuFrame, "Current account is closed!");
            }
        } else if (e.getActionCommand().equals("Pay-friend")) {
            String accountid = JOptionPane.showInputDialog(new AccountSelectUI(customerId, true));
            if(accountid == null)return;
            this.account = accountDao.queryAccountById(Integer.valueOf(accountid));
            // If current account is not closed
            if (!account.getClosed()) {
                // Open Pay-friend ui
                PayFriendUI payFriendUI = new PayFriendUI(account, customerId);
            } else {
                JOptionPane.showMessageDialog(menuFrame, "Current account is closed!");
            }
        } else if (e.getActionCommand().equals("Transfer")) {
            String accountid = JOptionPane.showInputDialog(new AccountSelectUI(customerId, false));
            if(accountid == null)return;
            this.account = accountDao.queryAccountById(Integer.valueOf(accountid));
            // If current account is not closed
            if (!account.getClosed()) {
                // Open transfer ui
                TransferUI transferUI = new TransferUI(account, customerId);
            } else {
                JOptionPane.showMessageDialog(menuFrame, "Current account is closed!");
            }
        } else if (e.getActionCommand().equals("Wire")) {
            String accountid = JOptionPane.showInputDialog(new AccountSelectUI(customerId, false));
            if(accountid == null)return;
            this.account = accountDao.queryAccountById(Integer.valueOf(accountid));
            // If current account is not closed
            if (!account.getClosed()) {
                // Open transfer ui
                WireUI wireUI = new WireUI(account, customerId);
            } else {
                JOptionPane.showMessageDialog(menuFrame, "Current account is closed!");
            }
        }
    }
}
