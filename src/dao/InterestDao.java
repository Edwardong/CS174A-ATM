package dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class InterestDao {
    public static BigDecimal getCheckingInterest(){
        DBExecutor dbExecutor = new DBExecutor();
        String sql_get = "SELECT * FROM interests";
        List<Map<String, Object>> id = dbExecutor.query(sql_get);
        BigDecimal checkingInterest = new BigDecimal((String)id.get(0).get("CHECKING"));
        return checkingInterest;
    }

    public static BigDecimal getSavingsInterest(){
        DBExecutor dbExecutor = new DBExecutor();
        String sql_get = "SELECT * FROM interests";
        List<Map<String, Object>> id = dbExecutor.query(sql_get);
        BigDecimal checkingInterest = new BigDecimal((String)id.get(0).get("SAVINGS"));
        return checkingInterest;
    }

    public static boolean setCheckingInterest(BigDecimal interest){
        DBExecutor dbExecutor = new DBExecutor();
        String sql = "UPDATE interests SET checking=" + interest.toString() + " WHERE id=1";
        return dbExecutor.runUpdate(sql);
    }

    public static boolean setSavingsInterest(BigDecimal interest){
        DBExecutor dbExecutor = new DBExecutor();
        String sql = "UPDATE interests SET savings=" + interest.toString() + " WHERE id=1";
        return dbExecutor.runUpdate(sql);
    }
}
