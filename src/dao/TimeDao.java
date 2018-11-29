package dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TimeDao {
    public static int getCurrentAddTime(){
        // Get the result of table add_time
        DBExecutor dbExecutor = new DBExecutor();
        String sql_getTime = "SELECT * FROM add_time";
        List<Map<String, Object>> id = dbExecutor.query(sql_getTime);
        // Get current days
        BigDecimal currentId = (BigDecimal)id.get(0).get("VALUE");
        return Integer.valueOf(currentId.toString());
    }

    public static Date getCurrentTime(){
        int addDays = getCurrentAddTime();
        long addMillis = (long)addDays * 24 * 3600 * 1000;
        return new Date(System.currentTimeMillis() + addMillis);
    }

    public static void addTime(int days){
        int currentAdd = TimeDao.getCurrentAddTime();
        currentAdd += days;
        DBExecutor dbExecutor = new DBExecutor();
        String sql_updateId = "UPDATE add_time SET value=" + currentAdd + " WHERE id=1";
        dbExecutor.runUpdate(sql_updateId);
    }
}
