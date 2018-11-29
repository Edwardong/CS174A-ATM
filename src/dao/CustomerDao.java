package dao;

import model.Customer;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * This is a class for accessing database and
 * executing created sql statement to deal with customer
 */
public class CustomerDao {
    private DBExecutor dbExecutor;

    public CustomerDao(){
        dbExecutor = new DBExecutor();
    }

    public Customer queryCustomerByTax(String tax_id){
        String sql = "SELECT * FROM customers WHERE tax_id ='" + tax_id + "'";
        List<Map<String, Object>> customerList = dbExecutor.query(sql);
        if(customerList == null || customerList.size() == 0)
            return null;
        Customer customer = new Customer();
        customer.setTax_id((String)customerList.get(0).get("TAX_ID"));
        customer.setName((String)customerList.get(0).get("NAME"));
        customer.setAddress((String)customerList.get(0).get("ADDRESS"));
        return customer;
    }

    public boolean addCustomer(Customer customer){
        String sql = "INSERT INTO customers VALUES(?,?,?)";
        Object[] objects = {customer.getTax_id(), customer.getName(), customer.getAddress()};
        // Set initial pin
        PinDao pinDao = new PinDao();
        pinDao.initialPin(customer.getTax_id());
        return dbExecutor.runUpdate(sql, objects);
    }

    public Customer[] queryCustomerByAccountId(String accountId){
        String sql = "SELECT * FROM customers WHERE tax_id in (SELECT customers_accounts.customer_id FROM customers_accounts WHERE account_id='" + accountId + "')";
        return doQueryCustomersSQL(sql);
    }

    public Customer[] queryCustomerWithoutAccount(){
        String sql = "SELECT * FROM customers WHERE tax_id not in (SELECT customers_accounts.customer_id from customers_accounts where customers_accounts.customer_id = customers.tax_id)";
        return doQueryCustomersSQL(sql);
    }

    public boolean deleteCustomerWithouAccount(){
        String sql = "DELETE FROM customers WHERE tax_id not in (SELECT customers_accounts.customer_id from customers_accounts where customers_accounts.customer_id = customers.tax_id)";
        return dbExecutor.runUpdate(sql);
    }

    private Customer[] doQueryCustomersSQL(String sql){
        List<Map<String, Object>> customerList = dbExecutor.query(sql);
        if(customerList == null || customerList.size() == 0)
            return null;
        Customer[] customers = new Customer[customerList.size()];
        for (int i = 0; i < customers.length; i++) {
            Customer customer = new Customer();
            customer.setTax_id((String)customerList.get(i).get("TAX_ID"));
            customer.setName((String)customerList.get(i).get("NAME"));
            customer.setAddress((String)customerList.get(i).get("ADDRESS"));
            customers[i] = customer;
        }
        return customers;
    }


}
