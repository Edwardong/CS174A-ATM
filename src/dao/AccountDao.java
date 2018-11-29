package dao;

import model.Account;
import model.AccountType;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * This is a class for accessing database and
 * executing created sql statement to deal with account
 */
public class AccountDao {
    private DBExecutor dbExecutor;

    public AccountDao(){
        dbExecutor = new DBExecutor();
    }

    public boolean addAcount(Account account){
        String sql = "INSERT INTO accounts VALUES(?,?,?,?,?,?,?)";
        // If account is closed then this flag is 0
        int ifClosed = 0;
        if(!account.getClosed())
            ifClosed = 1;
        Object[] objects = {account.getId(), account.getType().toString(), account.getPrimary_owner(), account.getBank_branch_name(), account.getBalance().toString(), account.getInterest_rate().toString(), ifClosed};
        return dbExecutor.runUpdate(sql, objects);
    }

    public Account queryAccountById(int id){
        String sql = "SELECT * FROM accounts WHERE id ='" + id + "'";
        List<Map<String, Object>> accountList = dbExecutor.query(sql);
        if(accountList == null || accountList.size() == 0)
            return null;
        Account account = new Account();
        account.setId(id);
        account.setType(AccountType.translate((String) accountList.get(0).get("TYPE")));
        account.setPrimary_owner((String)accountList.get(0).get("PRIMARY_OWNER"));
        account.setBank_branch_name((String)accountList.get(0).get("BANK_BRANCH_NAME"));
        account.setBalance(new BigDecimal((String)accountList.get(0).get("BALANCE")));
        account.setInterest_rate(new BigDecimal((String)accountList.get(0).get("INTEREST_RATE")));
        int closed = Integer.valueOf(((BigDecimal)accountList.get(0).get("CLOSED")).toString());
        account.setClosed(closed == 0 ? true : false);
        return account;
    }

    public Account[] queryAccountByCustomerId(String customerId){
        String sql = "SELECT * FROM accounts WHERE id in (SELECT customers_accounts.account_id FROM customers_accounts WHERE customer_id='" + customerId + "')";
        return doQueryAccountsSQL(sql);
    }

    public Account[] queryPrimaryAccountByCustomerId(String customerId){
        String sql = "SELECT * FROM accounts WHERE primary_owner='" + customerId + "'";
        return doQueryAccountsSQL(sql);
    }

    public Account[] queryClosedAccount(){
        String sql = "SELECT * FROM accounts WHERE closed=0";
        return doQueryAccountsSQL(sql);
    }

    public Account[] queryInterestAccount(){
        String sql = "SELECT * FROM accounts WHERE (type = 'INTEREST_CHECKING_ACCOUNT' OR type = 'SAVING_ACCOUNT') AND closed=1";
        return doQueryAccountsSQL(sql);
    }

    private Account[] doQueryAccountsSQL(String sql){
        List<Map<String, Object>> accountList = dbExecutor.query(sql);
        if(accountList == null || accountList.size() == 0)
            return null;
        Account[] accounts = new Account[accountList.size()];
        for (int i = 0; i < accounts.length; i++) {
            Account account = new Account();
            account.setId(Integer.valueOf(((BigDecimal)accountList.get(i).get("ID")).toString()));
            account.setType(AccountType.translate((String) accountList.get(i).get("TYPE")));
            account.setPrimary_owner((String)accountList.get(i).get("PRIMARY_OWNER"));
            account.setBank_branch_name((String)accountList.get(i).get("BANK_BRANCH_NAME"));
            account.setBalance(new BigDecimal((String)accountList.get(i).get("BALANCE")));
            account.setInterest_rate(new BigDecimal((String)accountList.get(i).get("INTEREST_RATE")));
            int closed = Integer.valueOf(((BigDecimal)accountList.get(i).get("CLOSED")).toString());
            account.setClosed(closed == 0 ? true : false);
            accounts[i] = account;
        }
        return accounts;
    }

    public boolean deleteAllClosedAccount(){
        String sql = "DELETE FROM accounts WHERE closed=0";
        return dbExecutor.runUpdate(sql);
    }

    public boolean updateBalance(int accountId, BigDecimal newBalance){
        String sql = "UPDATE accounts SET balance=" + newBalance.toString() + " WHERE id=" + accountId;
        return dbExecutor.runUpdate(sql);
    }

    public boolean updateState(int accountId, boolean closed){
        int state = closed ? 0 : 1;
        String sql = "UPDATE accounts SET closed=" + state + " WHERE id=" + accountId;
        return dbExecutor.runUpdate(sql);
    }

}
