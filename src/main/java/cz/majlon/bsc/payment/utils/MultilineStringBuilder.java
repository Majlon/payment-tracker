package cz.majlon.bsc.payment.utils;

import cz.majlon.bsc.payment.domain.Balance;
import cz.majlon.bsc.payment.domain.Payment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class MultilineStringBuilder {

    private static String separator = System.getProperty("line.separator");

    StringBuilder content;

    public MultilineStringBuilder() {
        this.content = new StringBuilder();
    }

    public MultilineStringBuilder nextLine(String line) {
        this.content.append(line);
        this.content.append(separator);
        return this;
    }

    public MultilineStringBuilder emptyLine() {
        this.content.append(" ");
        this.content.append(separator);
        return this;
    }

    public MultilineStringBuilder lineSeparator() {
        this.content.append("---------------------------------------------------");
        this.content.append(separator);
        return this;
    }

    public MultilineStringBuilder historyTableHeader() {
        this.content.append("/-----------------  History  ----------------------\\");
        this.content.append(separator);
        this.content.append("|--ID--|---CODE---|---AMOUNT---|---CREATION DATE--|");
        this.content.append(separator);
        this.lineSeparator();
        return this;
    }

    public MultilineStringBuilder historyTableRows(Collection<Payment> payments) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

        for (Payment eachPayment : payments) {
            this.content.append("|");
            this.content.append(String.format("%1$6s", eachPayment.getId())).append("|");
            this.content.append(String.format("%1$10s", eachPayment.getCurrencyCode())).append("|");
            this.content.append(String.format("%1$12s", eachPayment.getAmount())).append("|");
            this.content.append(String.format("%1$18s", formatter.format(eachPayment.getDateCreated()))).append("| ");
            this.content.append(separator);
        }
        this.lineSeparator();
        return this;
    }

    public MultilineStringBuilder balanceTableHeader() {
        this.content.append("/----------------  Balance  ----------------------\\");
        this.content.append(separator);
        this.content.append("|--CODE--|----AMOUNT----|------EXCHANGE RATE------|");
        this.content.append(separator);
        this.lineSeparator();
        return this;
    }

    public MultilineStringBuilder balanceTableRows(Collection<Balance> balance) {
        for (Balance each : balance) {
            this.content.append("|");
            this.content.append(String.format("%1$8s", each.getCurrencyCode())).append("|");
            this.content.append(String.format("%1$14s", each.getAmount())).append("|");
            this.content.append(String.format("%1$25s", getExchangeRate(each))).append("|");
            this.content.append(separator);
        }
        this.lineSeparator();
        return this;
    }

    private String getExchangeRate(Balance balance) {
        if (balance.getExchangeRate() != null) {
            final MathContext round = new MathContext(10);
            final BigDecimal rateValue = new BigDecimal(balance.getExchangeRate());
            return " (USD :" + (balance.getAmount().multiply(rateValue)).round(round).toPlainString() + ")";
        } else {
            return "N/A";
        }
    }

    public MultilineStringBuilder tableFooter(int recordCount) {
        this.content.append("| Record count:");
        this.content.append(String.format("%1$34s", recordCount)).append(" |");
        this.content.append(separator);
        this.lineSeparator();
        return this;
    }

    public String make() {
        return this.content.toString();
    }
}
