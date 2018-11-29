package bankteller;

import dao.*;
import model.Account;
import model.AccountType;
import model.Customer;

import javax.swing.*;

public class DeleteAccountUI extends JPanel {
    public DeleteAccountUI(){
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure?");
        if(choice == 0){
            // Firstly find all closed account;
            AccountDao accountDao = new AccountDao();
            Account[] accounts = accountDao.queryClosedAccount();
            if(accounts == null || accounts.length == 0){
                JOptionPane.showMessageDialog(this, "No closed account!");
                return;
            }
            // For each account, delete the relation between accounts and customers
            CustAccoDao custAccoDao = new CustAccoDao();
            PocketDao pocketDao = new PocketDao();
            for (int i = 0; i < accounts.length; i++) {
                custAccoDao.deleteRelationByAccountId(accounts[i].getId());
                if(accounts[i].getType() == AccountType.POCKET_ACCOUNT){
                    pocketDao.deleteRelation(accounts[i].getId());
                }
            }
            // Then delete all closed accounts;
            accountDao.deleteAllClosedAccount();
            // Then find if there is a customer without accounts
            CustomerDao customerDao = new CustomerDao();
            TransactionDao transactionDao = new TransactionDao();
            Customer[] customers = customerDao.queryCustomerWithoutAccount();
            if(customers != null && customers.length != 0){
                for (int i = 0; i < customers.length; i++) {
                    transactionDao.deleteTransactionsByCustomerId(customers[i].getTax_id());
                }
            }
            customerDao.deleteCustomerWithouAccount();
            JOptionPane.showMessageDialog(this, "Closed Accounts and Customers is successfully deleted!");
        }
    }
}
