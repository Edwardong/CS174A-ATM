package dao;

import java.util.List;
import java.util.Map;

public class PinDao {
    private DBExecutor dbExecutor;

    public PinDao(){
        dbExecutor = new DBExecutor();
    }

    public boolean initialPin(String customerId){
        String sql = "INSERT INTO password VALUES(?,?)";
        String initPin = "1717";
        Object[] objects = {customerId, initPin};
        return dbExecutor.runUpdate(sql, objects);
    }

    public boolean verifyPin(String customerId, String pin){
        String sql = "SELECT * FROM password WHERE customer_id=" + customerId;
        List<Map<String, Object>> list = dbExecutor.query(sql);
        if(list != null && list.size() != 0){
            String exactPin = (String)list.get(0).get("PIN");
            return exactPin.equals(pin);
        }
        return false;
    }

    public boolean setPin(String customerId, String oldPin, String newPin){
        if(verifyPin(customerId, oldPin)){
            String sql = "UPDATE password SET pin='" + newPin + "' WHERE customer_id='" + customerId + "'";
            return dbExecutor.runUpdate(sql);
        }
        return false;
    }
}
