package Util;

public class MoneyUtil {
    /**
     * Verify if input is correct
     * @param str
     * @return
     */
    public static boolean verifyMoney(String str){
        String re = "([1-9]\\d*|0)(\\.\\d{1,2})?";
        return str.matches(re);
    }
}
