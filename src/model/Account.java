package model;

import java.math.BigDecimal;

/**
 * This is a class for describing a account. A account contains:
 * 1. Id
 * 2. Type(4 types) -- STUDENT_CHECKING_ACCOUNT, INTEREST_CHECKING_ACCOUNT, SAVING_ACCOUNT, POCKET_ACCOUNT
 * 3. Primary owner
 * 4. Bank branch name
 * 5. Balance -- Because double is not accurate, use BigDecimal
 * 6. Interest rate -- Because double is not accurate, use BigDecimal
 * 7. Closed -- If the account is closed
 */
public class Account {
    private int id;

    private AccountType type;

    private String primary_owner;

    private String bank_branch_name;

    private BigDecimal balance;

    private BigDecimal interest_rate;

    private Boolean closed;

    public Account(){

    }

    public Account(int id, AccountType type, String primary_owner, String bank_branch_name, BigDecimal balance, BigDecimal interest_rate, Boolean closed) {
        this.id = id;
        this.type = type;
        this.primary_owner = primary_owner;
        this.bank_branch_name = bank_branch_name;
        this.balance = balance;
        this.interest_rate = interest_rate;
        this.closed = closed;
    }

    // When create a new account, the id of the account should be provided by database instead of giving it
    public Account(AccountType type, String primary_owner, String bank_branch_name, BigDecimal balance, BigDecimal interest_rate, Boolean closed) {
        // Set id to be -1 for initializing
        this.id = -1;
        this.type = type;
        this.primary_owner = primary_owner;
        this.bank_branch_name = bank_branch_name;
        this.balance = balance;
        this.interest_rate = interest_rate;
        this.closed = closed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getPrimary_owner() {
        return primary_owner;
    }

    public void setPrimary_owner(String primary_owner) {
        this.primary_owner = primary_owner;
    }

    public String getBank_branch_name() {
        return bank_branch_name;
    }

    public void setBank_branch_name(String bank_branch_name) {
        this.bank_branch_name = bank_branch_name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getInterest_rate() {
        return interest_rate;
    }

    public void setInterest_rate(BigDecimal interest_rate) {
        this.interest_rate = interest_rate;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }
}
