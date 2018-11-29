package bankteller;

import dao.TransactionDao;

import javax.swing.*;

public class DeleteTransactionsUI extends JPanel {
    public DeleteTransactionsUI(){
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure?");
        if(choice == 0){
            TransactionDao transactionDao = new TransactionDao();
            transactionDao.deleteAllTransactions();
            JOptionPane.showMessageDialog(this, "Transaction is successfully deleted!");
        }
    }
}
