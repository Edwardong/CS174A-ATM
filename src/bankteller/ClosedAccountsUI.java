package bankteller;

import dao.AccountDao;
import dao.CustomerDao;
import model.Account;
import model.Customer;

import javax.swing.*;
import java.awt.*;

public class ClosedAccountsUI extends JPanel {

    private JPanel accountPanel;

    public ClosedAccountsUI(){
        accountPanel = new JPanel();

        accountPanel.add(new JLabel("Account ID"));
        accountPanel.add(new JLabel("Account Type"));
        accountPanel.add(new JLabel("Primary Owner Name"));
        accountPanel.add(new JLabel("Bank Branch Name"));
        int line = 1;
        AccountDao accountDao = new AccountDao();
        Account[] accounts = accountDao.queryClosedAccount();
        CustomerDao customerDao = new CustomerDao();
        if(accounts != null && accounts.length != 0){
            for (int i = 0; i < accounts.length; i++) {
                accountPanel.add(new JLabel(String.valueOf(accounts[i].getId())));
                accountPanel.add(new JLabel(String.valueOf(accounts[i].getType())));
                accountPanel.add(new JLabel((customerDao.queryCustomerByTax(accounts[i].getPrimary_owner())).getName()));
                accountPanel.add(new JLabel(accounts[i].getBank_branch_name()));
                line++;
            }
        }
        accountPanel.setLayout(new GridLayout(line,4));

        this.add(accountPanel);
        this.setLayout(new FlowLayout());
        this.setVisible(true);
    }
}
