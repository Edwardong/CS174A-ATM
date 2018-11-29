package bankteller;

import dao.CustomerDao;
import dao.TransactionDao;
import model.Customer;
import model.Transaction;
import model.TransactionType;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTERUI extends JPanel{
    private JPanel accountPanel;

    public DTERUI(){
        accountPanel = new JPanel();

        TransactionDao transactionDao = new TransactionDao();
        Transaction[] transactions = transactionDao.queryTransactionsDuringCurrentMonth();

        Map<String, BigDecimal> sumMap = new HashMap<>();
        if(transactions != null && transactions.length != 0){
            for (int i = 0; i < transactions.length; i++) {
                if(transactions[i].getTransactionType() == TransactionType.DEPOSIT ||
                        transactions[i].getTransactionType() == TransactionType.TRANSFER ||
                        transactions[i].getTransactionType() == TransactionType.WIRE){
                    BigDecimal currentMoney = transactions[i].getActual_money();
                    String customerId = transactions[i].getCustomerId();
                    if(!sumMap.keySet().contains(customerId)){
                        sumMap.put(customerId, currentMoney);
                    }else{
                        currentMoney = currentMoney.add(sumMap.get(customerId));
                        sumMap.put(customerId, currentMoney);
                    }
                }
            }
        }
        List<String> customerIds = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry :
                sumMap.entrySet()) {
            BigDecimal sumMoney = entry.getValue();
            if(sumMoney.compareTo(new BigDecimal("10000")) > 0){
                customerIds.add(entry.getKey());
            }
        }


        accountPanel.add(new JLabel("Tax id"));
        accountPanel.add(new JLabel("Name"));
        accountPanel.add(new JLabel("Address"));
        accountPanel.add(new JLabel("Sum"));
        int line = 1;
        CustomerDao customerDao = new CustomerDao();
        if(customerIds.size() != 0){
            for (int i = 0; i < customerIds.size(); i++) {
                Customer customer = customerDao.queryCustomerByTax(customerIds.get(i));
                accountPanel.add(new JLabel(customer.getTax_id()));
                accountPanel.add(new JLabel(customer.getName()));
                accountPanel.add(new JLabel(customer.getAddress()));
                accountPanel.add(new JLabel(sumMap.get(customer.getTax_id()).setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
                line++;
            }
        }
        accountPanel.setLayout(new GridLayout(line,4));
        this.add(accountPanel);
        this.setLayout(new FlowLayout());
        this.setVisible(true);
    }

//    public static void main(String[] args) {
//        TransactionDao transactionDao = new TransactionDao();
//        //Transaction[] transactions = transactionDao.queryTransactionsDuringCurrentMonth();
//        Transaction[] transactions = transactionDao.queryTransactionsByPrimaryOwner("344151573");
//        System.out.println(transactions.length);
//    }
}
