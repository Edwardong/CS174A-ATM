package dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PocketDao {
    private DBExecutor dbExecutor;

    public PocketDao(){
        dbExecutor = new DBExecutor();
    }

    public boolean addAssociation(int accountid, int associationid){
        String sql = "INSERT INTO pocket_association VALUES(?,?,?)";
        // If account is closed then this flag is 0
        Object[] objects = {IDCreator.getNextId(), accountid, associationid};
        return dbExecutor.runUpdate(sql, objects);
    }

    public int queryAsscAccountByPocketAccount(int accountid){
        String sql = "SELECT * FROM pocket_association WHERE account_id=" + accountid;
        List<Map<String, Object>> list = dbExecutor.query(sql);
        if(list != null && list.size() != 0){
            return ((BigDecimal)list.get(0).get("ASSOCIATION_ID")).intValue();
        }
        return -1;
    }

    public int queryPocketAccountByAsscAccount(int associationid){
        String sql = "SELECT * FROM pocket_association WHERE association_id=" + associationid;
        List<Map<String, Object>> list = dbExecutor.query(sql);
        if(list != null && list.size() != 0){
            return ((BigDecimal)list.get(0).get("ACCOUNT_ID")).intValue();
        }
        return -1;
    }

    public boolean updateBalance(int pocketId, BigDecimal newBalance){
        String sql = "UPDATE accounts SET balance=" + newBalance.toString() + " WHERE id=" + pocketId;
        return dbExecutor.runUpdate(sql);
    }

    public boolean deleteRelation(int accountId){
        String sql = "DELETE FROM pocket_association WHERE account_id=" + String.valueOf(accountId);
        return dbExecutor.runUpdate(sql);
    }
}
