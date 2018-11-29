package dao;

import model.Transaction;
import model.TransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This is a class for accessing database and
 * executing created sql statement to deal with customer
 */
public class TransactionDao {
    private DBExecutor dbExecutor;

    public TransactionDao(){
        dbExecutor = new DBExecutor();
    }

    public boolean addTransaction(Transaction transaction){
        // Need to translate the date in java to oracle
        java.util.Date sysDate = transaction.getTran_date();
        java.sql.Date date = new java.sql.Date(sysDate.getTime());
        String sql = "INSERT INTO transactions VALUES(?,?,?,?,?,?,?,?,?,?)";
        Object[] objects = {IDCreator.getNextId(), date, transaction.getTransactionType().toString(), transaction.getCustomerId(), transaction.getFrom_id(), transaction.getTo_id(), transaction.getMoney().toString(), transaction.getActual_money().toString(), transaction.getFee().toString(), transaction.getCheck_number()};
        return dbExecutor.runUpdate(sql, objects);
    }

    public Transaction[] queryTransactionsByCheckNumber(String checkNumber){
        String sql = "SELECT * FROM transactions WHERE check_number ='" + checkNumber + "'";
        return doQueryAccountsSQL(sql);
    }

    public Transaction[] queryTransactionsByPrimaryOwner(String customer_id){
        String view = "(SELECT customers_accounts.account_id FROM customers_accounts WHERE customer_id='" + customer_id + "')";
        String sql = "SELECT * FROM transactions WHERE from_id in " + view + " OR to_id in" + view;
        return doQueryAccountsSQL(sql);
    }

    public Transaction[] queryTransactionsDuringCurrentMonth(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String date = sdf.format(TimeDao.getCurrentTime());
        String sql = "SELECT * FROM transactions WHERE TO_CHAR(tran_date,'yyyy-MM')='" + date + "'";
        return doQueryAccountsSQL(sql);
    }

    public Transaction[] queryTransactionsDuringCurrentMonthWithAccountId(int accountId){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String date = sdf.format(TimeDao.getCurrentTime());
        String sql = "SELECT * FROM transactions WHERE (from_id =" + accountId + " OR to_id =" + accountId + ") AND TO_CHAR(tran_date,'yyyy-MM')='" + date + "'";
        return doQueryAccountsSQL(sql);
    }

    public Transaction[] queryTransactionsByAccountId(int accountId){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String date = sdf.format(TimeDao.getCurrentTime());
        String sql = "SELECT * FROM transactions WHERE from_id= " + accountId + " OR to_id=" + accountId;
        return doQueryAccountsSQL(sql);
    }

    private Transaction[] doQueryAccountsSQL(String sql){
        List<Map<String, Object>> transactionList = dbExecutor.query(sql);
        if(transactionList == null || transactionList.size() == 0)
            return null;
        Transaction[] transactions = new Transaction[transactionList.size()];
        for (int i = 0; i < transactions.length; i++) {
            Transaction transaction = new Transaction();
            transaction.setId(Integer.valueOf((transactionList.get(i).get("ID")).toString()));
            transaction.setTran_date((Timestamp)transactionList.get(i).get("TRAN_DATE"));
            transaction.setTransactionType(TransactionType.valueOf((String)transactionList.get(i).get("TYPE")));
            transaction.setCustomerId((String)transactionList.get(i).get("CUSTOMER_ID"));
            transaction.setFrom_id(Integer.valueOf((transactionList.get(i).get("FROM_ID")).toString()));
            transaction.setTo_id(Integer.valueOf((transactionList.get(i).get("TO_ID")).toString()));
            transaction.setMoney(new BigDecimal((String)transactionList.get(i).get("MONEY")));
            transaction.setActual_money(new BigDecimal((String)transactionList.get(i).get("ACTUAL_MONEY")));
            transaction.setFee(new BigDecimal((String)transactionList.get(i).get("FEE")));
            transaction.setCheck_number((String)transactionList.get(i).get("CHECK_NUMBER"));
            transactions[i] = transaction;
        }
        return transactions;
    }


    public boolean deleteTransactionsByCustomerId(String customerId){
        String sql = "DELETE FROM transactions WHERE customer_id='" + customerId + "'";
        return dbExecutor.runUpdate(sql);
    }

    public boolean deleteAllTransactions(){
        String sql = "DELETE FROM transactions";
        return dbExecutor.runUpdate(sql);
    }


}
