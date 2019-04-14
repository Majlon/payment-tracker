package cz.majlon.bsc.payment.utils;

import cz.majlon.bsc.payment.domain.Balance;
import cz.majlon.bsc.payment.domain.Payment;

import java.util.Collection;

public class Messages {

    public static String welcomeMessage() {
        MultilineStringBuilder builder = new MultilineStringBuilder();
        return builder
                .nextLine("Welcome in payment tracker! type help")
                .lineSeparator()
                .make();
    }

    public static String helpMessage() {
        MultilineStringBuilder builder = new MultilineStringBuilder();
        return builder
                .nextLine("/----------------    Help    ---------------------\\")
                .emptyLine()
                .nextLine("All commands and arguments are separated by white-spaces")
                .lineSeparator()
                .nextLine("Command overview:")
                .emptyLine()
                .nextLine("* add - allows user to add new payment. expected format is Currency code then amount")
                .nextLine("* get - allows user to get single payment by id")
                .nextLine("* rem - allows user to remove single payment by id")
                .nextLine("* rate - allows user to add exchange value to USD ")
                .nextLine("* history - prints transaction history")
                .nextLine("* balance - prints current balance on account")
                .nextLine("* import - imports payments from specified file")
                .nextLine("* export - export payments to specified file")
                .nextLine("* quit - Terminates application")
                .make();
    }

    public static String balance(Collection<Balance> balance) {
        MultilineStringBuilder builder = new MultilineStringBuilder();
        return builder
                .balanceTableHeader()
                .balanceTableRows(balance)
                .tableFooter(balance.size())
                .make();

    }

    public static String history(Collection<Payment> payments) {
        MultilineStringBuilder builder = new MultilineStringBuilder();
        return builder
                .historyTableHeader()
                .historyTableRows(payments)
                .tableFooter(payments.size())
                .make();
    }
}


