package cz.majlon.bsc.payment.domain;

import java.math.BigDecimal;

public class Balance {

    private String currencyCode;
    private BigDecimal amount;
    private Double exchangeRate;

    public Balance(String currencyCode, BigDecimal amount, Double exchangeRate) {
        this.currencyCode = currencyCode;
        this.amount = amount;
        this.exchangeRate = exchangeRate;
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

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "currencyCode='" + currencyCode + '\'' +
                ", amount=" + amount +
                ", exchangeRate=" + exchangeRate +
                '}';
    }
}
