package atm;

import dao.AccountDao;
import model.Account;
import model.AccountType;

import javax.swing.*;
import java.awt.*;

public class AccountSelectUI extends JPanel{
    // private JPanel accountSelectPanel;

    private JPanel accountsPanel;

    private JPanel describePanel;

    private Account[] accounts;

    public AccountSelectUI(String taxId, boolean ifPocket) {
        // accountSelectPanel = new JPanel();
        accountsPanel = new JPanel();
        describePanel = new JPanel();
        describePanel.add(new JLabel("Please type in account id"));

        AccountDao accountDao = new AccountDao();
        // Get all accounts belong to current customer
        accounts = accountDao.queryAccountByCustomerId(taxId);
        if(accounts != null && accounts.length != 0){
            accountsPanel.add(new Label("Account Id"));
            accountsPanel.add(new Label("Account Type"));
            accountsPanel.add(new Label("Bank Branch Name"));
            accountsPanel.add(new Label("Balance"));
            accountsPanel.add(new Label("Closed"));
            int count = 0;
            for (int i = 0; i < accounts.length; i++) {
                if((accounts[i].getType() == AccountType.POCKET_ACCOUNT)){
                    if(ifPocket){
                        count++;
                        accountsPanel.add(new Label(String.valueOf(accounts[i].getId())));
                        accountsPanel.add(new Label(String.valueOf(accounts[i].getType())));
                        accountsPanel.add(new Label(accounts[i].getBank_branch_name()));
                        accountsPanel.add(new Label(accounts[i].getBalance().toString()));
                        accountsPanel.add(new Label(accounts[i].getClosed() ? "true" : "false"));
                    }
                }
                else{
                    if(!ifPocket){
                        count++;
                        accountsPanel.add(new Label(String.valueOf(accounts[i].getId())));
                        accountsPanel.add(new Label(String.valueOf(accounts[i].getType())));
                        accountsPanel.add(new Label(accounts[i].getBank_branch_name()));
                        accountsPanel.add(new Label(accounts[i].getBalance().toString()));
                        accountsPanel.add(new Label(accounts[i].getClosed() ? "true" : "false"));
                    }
                }
            }
            accountsPanel.setLayout(new GridLayout(count + 1, 5));
            accountsPanel.setVisible(true);

            this.setBounds(300,300,1000,400);
            this.add(accountsPanel);
            this.add(describePanel);
            this.setVisible(true);
            this.setLayout(new GridLayout(2,1));
        }
    }

    public static void main(String[] args) {
        //System.out.println(JOptionPane.showInputDialog(new AccountSelectUI("361721022")));
    }
}
