package dao;

/**
 * This is a class for accessing database and
 * executing created sql statement to deal with the relationship between customers and accounts;
 */
public class CustAccoDao {
    private DBExecutor dbExecutor;

    public CustAccoDao(){
        dbExecutor = new DBExecutor();
    }

    public boolean addRelation(String customerid, int accountid){
        String sql = "INSERT INTO customers_accounts VALUES(?,?,?)";
        Object[] objects = {IDCreator.getNextId(), customerid, accountid};
        return dbExecutor.runUpdate(sql, objects);
    }

    public boolean deleteRelationByAccountId(int accountId){
        String sql = "DELETE FROM customers_accounts WHERE account_id=" + String.valueOf(accountId);
        return dbExecutor.runUpdate(sql);
    }

}
