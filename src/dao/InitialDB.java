package dao;

import model.Account;
import model.AccountType;
import model.Customer;

import java.math.BigDecimal;

public class InitialDB {
    public static void initDB() {
        // Create table
        String sql_create_table_customers =
                "CREATE TABLE customers("
                        + "tax_id VARCHAR2(20) PRIMARY KEY NOT NULL,"
                        + "name VARCHAR2(20),"
                        + "address VARCHAR2(50)"
                        + ")";
        String sql_create_table_accounts =
                "CREATE TABLE accounts(" +
                        "id INT PRIMARY KEY NOT NULL," +
                        "type VARCHAR2(30)," +
                        "primary_owner VARCHAR2(20) NOT NULL REFERENCES customers(tax_id)," +
                        "bank_branch_name VARCHAR2(50)," +
                        "balance VARCHAR2(20)," +
                        "interest_rate VARCHAR2(20)," +
                        "closed INT" +
                        ")";
        String sql_create_table_transactions =
                "CREATE TABLE transactions("
                        + "id INT PRIMARY KEY NOT NULL,"
                        + "tran_date DATE,"
                        + "type VARCHAR2(20),"
                        + "customer_id VARCHAR(20) NOT NULL REFERENCES customers(tax_id),"
                        + "from_id INT,"
                        + "to_id INT,"
                        + "money VARCHAR2(20),"
                        + "actual_money VARCHAR2(20),"
                        + "fee VARCHAR2(20),"
                        + "check_number VARCHAR2(20)"
                        + ")";
        String sql_create_table_customer_accounts =
                "CREATE TABLE customers_accounts(" +
                        "id INT PRIMARY KEY NOT NULL," +
                        "customer_id VARCHAR(20) NOT NULL REFERENCES customers(tax_id)," +
                        "account_id INT NOT NULL REFERENCES accounts(id)" +
                        ")";
        String sql_create_table_id_creator =
                "CREATE TABLE id_creator(" +
                        "id INT PRIMARY KEY NOT NULL," +
                        "value INT)";
        String initial_id_creator = "INSERT INTO id_creator VALUES(1, 10000)";
        String sql_create_table_password =
                "CREATE TABLE password(" +
                        "customer_id VARCHAR(20) PRIMARY KEY NOT NULL," +
                        "pin VARCHAR(4))";
        String sql_create_packet_association =
                "CREATE TABLE pocket_association(" +
                        "id INT PRIMARY KEY NOT NULL," +
                        "account_id INT NOT NULL REFERENCES accounts(id)," +
                        "association_id INT NOT NULL REFERENCES accounts(id))";
        String sql_create_time =
                "CREATE TABLE add_time(" +
                        "id INT PRIMARY KEY NOT NULL," +
                        "value INT)";
        String sql_create_interest =
                "CREATE TABLE interest(" +
                        "id INT PRIMARY KEY NOT NULL," +
                        "checking VARCHAR(20)," +
                        "savings VARCHAR(20))";
        String initial_time = "INSERT INTO add_time VALUES(1, 0)";
        String inttial_interest = "INSERT INTO interest VALUES(1, '5.5', '7.5')";
        DBExecutor dbExecutor = new DBExecutor();
        System.out.println(dbExecutor.runUpdate(sql_create_table_customers));
        System.out.println(dbExecutor.runUpdate(sql_create_table_accounts));
        System.out.println(dbExecutor.runUpdate(sql_create_table_transactions));
        System.out.println(dbExecutor.runUpdate(sql_create_table_customer_accounts));
        System.out.println(dbExecutor.runUpdate(sql_create_table_id_creator));
        System.out.println(dbExecutor.runUpdate(initial_id_creator));
        System.out.println(dbExecutor.runUpdate(sql_create_table_password));
        System.out.println(dbExecutor.runUpdate(sql_create_packet_association));
        System.out.println(dbExecutor.runUpdate(sql_create_time));
        System.out.println(dbExecutor.runUpdate(initial_time));
        System.out.println(dbExecutor.runUpdate(sql_create_interest));
        System.out.println(dbExecutor.runUpdate(inttial_interest));
    }

    public static void initialPartA(){
        // Add customers
        Customer customer1 = new Customer("344151573", "Joe Pepsi", "3210 State St");
        Customer customer2 = new Customer("212431965", "Hurryson Ford", "678 State St");
        Customer customer3 = new Customer("188212217", "Magic Jordon", "3852 Court Rd");
        Customer customer4 = new Customer("203491209", "Nam-hoi Chung", "1997 People's St HK");
        Customer customer5 = new Customer("210389768", "Olive Stoner", "6689 El Colegio #151");
        Customer customer6 = new Customer("400651982", "Pit Wilson", "911 State St");
        CustomerDao customerDao = new CustomerDao();
        customerDao.addCustomer(customer1);
        customerDao.addCustomer(customer2);
        customerDao.addCustomer(customer3);
        customerDao.addCustomer(customer4);
        customerDao.addCustomer(customer5);
        customerDao.addCustomer(customer6);
        // Add accounts
        Account account1 = new Account(17431, AccountType.STUDENT_CHECKING_ACCOUNT, "344151573", "San Fransisco", new BigDecimal(1000), new BigDecimal("0.0"), false);
        Account account2 = new Account(41725,AccountType.INTEREST_CHECKING_ACCOUNT, "344151573", "Los Angeles", new BigDecimal(1000), new BigDecimal("5.5"), false);
        Account account3 = new Account(43942,AccountType.SAVING_ACCOUNT, "344151573", "Santa Barba1ra", new BigDecimal(1000), new BigDecimal("7.5"), false);
        Account account4 = new Account(53027,AccountType.POCKET_ACCOUNT, "344151573", "Goleta", new BigDecimal(1000), new BigDecimal("0.0"), false);
        Account account5 = new Account(54321, AccountType.STUDENT_CHECKING_ACCOUNT, "212431965", "Los Angeles", new BigDecimal(1000), new BigDecimal("0.0"), false);
        Account account6 = new Account(76543, AccountType.INTEREST_CHECKING_ACCOUNT, "212431965", "Santa Barbara", new BigDecimal(1000), new BigDecimal("5.5"), false);
        Account account7 = new Account(19023, AccountType.SAVING_ACCOUNT, "212431965", "San Fransisco", new BigDecimal(1000), new BigDecimal("7.5"), false);
        Account account8 = new Account(67521,AccountType.POCKET_ACCOUNT, "212431965", "Santa Barbara", new BigDecimal(1000), new BigDecimal("0.0"), false);
        AccountDao accountDao = new AccountDao();
        accountDao.addAcount(account1);
        accountDao.addAcount(account2);
        accountDao.addAcount(account3);
        accountDao.addAcount(account4);
        accountDao.addAcount(account5);
        accountDao.addAcount(account6);
        accountDao.addAcount(account7);
        accountDao.addAcount(account8);
    }

    public static void initialPartB(){
        // Add relations
        CustAccoDao custAccoDao = new CustAccoDao();
        custAccoDao.addRelation("344151573", 17431);
        custAccoDao.addRelation("344151573", 41725);
        custAccoDao.addRelation("344151573", 43942);
        custAccoDao.addRelation("344151573",53027);
        custAccoDao.addRelation("212431965",54321);
        custAccoDao.addRelation("212431965",76543);
        custAccoDao.addRelation("212431965",19023);
        custAccoDao.addRelation("212431965",67521);
        // Add other co-owner
        custAccoDao.addRelation("188212217", 17431);
        custAccoDao.addRelation("203491209", 17431);
        custAccoDao.addRelation("203491209", 41725);
        custAccoDao.addRelation("210389768", 41725);
        custAccoDao.addRelation("210389768", 43942);
        custAccoDao.addRelation("188212217", 53027);
        custAccoDao.addRelation("400651982", 53027);
        custAccoDao.addRelation("210389768", 54321);
        custAccoDao.addRelation("188212217", 76543);
        custAccoDao.addRelation("203491209", 76543);
        custAccoDao.addRelation("210389768", 76543);
        custAccoDao.addRelation("210389768", 19023);
        custAccoDao.addRelation("203491209", 67521);
        custAccoDao.addRelation("344151573", 67521);
    }

    public static void initialPartC(){
        // Add relation ship with pocket
        PocketDao pocketDao = new PocketDao();
        pocketDao.addAssociation(53027,17431);
        pocketDao.addAssociation(67521,54321);
        // Reset pin
        PinDao pinDao = new PinDao();
        pinDao.setPin("344151573", "1717", "3692");
        pinDao.setPin("212431965", "1717", "3532");
        pinDao.setPin("188212217", "1717", "7351");
        pinDao.setPin("203491209", "1717", "5340");
        pinDao.setPin("210389768", "1717", "8452");
        pinDao.setPin("400651982", "1717", "1821");
    }

    public static void dropAll(){
        String sql_pocket_association = "drop table pocket_association";
        String sql_id_creator = "drop table id_creator";
        String sql_customers_accounts = "drop table customers_accounts";
        String sql_password = "drop table password";
        String sql_transaction = "drop table transactions";
        String sql_accounts = "drop table accounts";
        String sql_customers = "drop table customers";
        String sql_time = "drop table add_time";
        String sql_interest = "drop table interest";
        DBExecutor dbExecutor = new DBExecutor();
        dbExecutor.runUpdate(sql_pocket_association);
        dbExecutor.runUpdate(sql_id_creator);
        dbExecutor.runUpdate(sql_customers_accounts);
        dbExecutor.runUpdate(sql_password);
        dbExecutor.runUpdate(sql_transaction);
        dbExecutor.runUpdate(sql_accounts);
        dbExecutor.runUpdate(sql_customers);
        dbExecutor.runUpdate(sql_time);
        dbExecutor.runUpdate(sql_interest);
    }

    public static void restartTransactions(){
        String sql_create_interest =
                "CREATE TABLE interest(" +
                        "id INT PRIMARY KEY NOT NULL," +
                        "checking VARCHAR(20)," +
                        "savings VARCHAR(20))";
        String inttial_interest = "INSERT INTO interest VALUES(1, '5.5', '7.5')";
        DBExecutor dbExecutor = new DBExecutor();
        dbExecutor.runUpdate(sql_create_interest);
        dbExecutor.runUpdate(inttial_interest);
    }

    public static void main(String[] args) {
        InitialDB.dropAll();
        InitialDB.initDB();
    }
}
