package cz.majlon.bsc.payment.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Payment {

    private Long id;
    private String currencyCode;
    private BigDecimal amount;
    private Date dateCreated;

    public Payment(String currencyCode, BigDecimal amount) {
        this.currencyCode = currencyCode;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", currencyCode='" + currencyCode + '\'' +
                ", amount=" + amount +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
