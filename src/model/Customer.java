package model;

/**
 * This is a class for describing a customer. A customer contains:
 * 1. Name
 * 2. Tax identification number
 * 3. Address
 * 4. Pin number
 */
public class Customer {
    private String tax_id;

    private String name;

    private String address;

    public Customer(){

    }

    public Customer(String tax_id, String name, String address) {
        this.tax_id = tax_id;
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
