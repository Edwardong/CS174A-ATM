package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This is a class for describing a transaction. A transaction contains:
 * 1. id
 * 2. The date of transaction
 * 3. Type -- DEPOSIT, TOP_UP, WITHDRAWAL, PURCHASE, TRANSFER, COLLECT, PAY_FRIEND, WIRE, WRITE_CHECK, ACCRUE_INTEREST
 * 4. The account which begins the transaction
 * 5. The account which accepts the transaction
 * 6. The amount of money in this transaction -- Because double is not accurate, use BigDecimal
 * 7. If the transaction is WRITE_CHECK, then there should be a check number
 */
public class Transaction {
    private int id;

    private Date tran_date;

    private TransactionType transactionType;

    private String customerId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    private int from_id;

    private int to_id;

    private BigDecimal money;

    private BigDecimal actual_money;

    private BigDecimal fee;

    public BigDecimal getActual_money() {
        return actual_money;
    }

    public void setActual_money(BigDecimal actual_money) {
        this.actual_money = actual_money;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    private String check_number;

    public Transaction(){

    }

    // When create a new transaction, the id of the account should be provided by database instead of giving it
//    public Transaction(Date tran_date, TransactionType transactionType, int from_id, int to_id, BigDecimal money, String check_number) {
//        // Set id to be -1 for initializing
//        this.id = -1;
//        this.tran_date = tran_date;
//        this.transactionType = transactionType;
//        this.from_id = from_id;
//        this.to_id = to_id;
//        this.money = money;
//        this.check_number = check_number;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTran_date() {
        return tran_date;
    }

    public void setTran_date(Date tran_date) {
        this.tran_date = tran_date;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getTo_id() {
        return to_id;
    }

    public void setTo_id(int to_id) {
        this.to_id = to_id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getCheck_number() {
        return check_number;
    }

    public void setCheck_number(String check_number) {
        this.check_number = check_number;
    }
}
