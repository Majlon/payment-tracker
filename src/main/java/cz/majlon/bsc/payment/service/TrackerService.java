package cz.majlon.bsc.payment.service;

import cz.majlon.bsc.payment.domain.Balance;
import cz.majlon.bsc.payment.domain.DataContainer;
import cz.majlon.bsc.payment.domain.Payment;
import cz.majlon.bsc.payment.utils.FileIO;
import cz.majlon.bsc.payment.utils.Messages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TrackerService {

    final private DataContainer dataContainer;
    final private AtomicLong idCounter;

    public TrackerService(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
        this.idCounter = new AtomicLong(0L);
    }

    /**
     * Basic payment saving.
     *
     * @param payment as Payment Object
     * @return saved Payment with set ID and Creation Date
     */
    public Payment savePayment(Payment payment) {
        System.out.println("Saving payment... ");

        if (payment != null) {
            payment.setId(this.idCounter.getAndIncrement());
            payment.setDateCreated(new Date());
            this.dataContainer.getData().add(payment);
            System.out.println("Payment successfully saved " + payment);
            return payment;
        } else {
            System.out.println("Payment was not saved!");
            return null;
        }
    }

    /**
     * Overloaded method for saving directly from command body. E.g.
     * this method contains also parsing to Payment from String.
     *
     * @param commandBody as String
     * @return saved Payment with set ID and Creation Date
     * @see cz.majlon.bsc.payment.service.TrackerService#savePayment(Payment)
     */
    public Payment savePayment(String commandBody) {
        try {
            final Payment payment2Save = parsePayment(commandBody);
            return this.savePayment(payment2Save);
        } catch (IllegalArgumentException ex) {
            System.out.println("Unable to parse payment: " + commandBody);
            return null;
        }
    }

    /**
     * Method for importing Payments from file. File is expected
     * to be in same directory. Also plain txt is expected.
     *
     * @param fileName as String to read from.
     * @return true if successful, false if otherwise.
     */
    public boolean importPayments(String fileName) {
        final BufferedReader fileReader;
        int counter = 0;
        try {
            fileReader = FileIO.getFileReader(fileName);
            String line = fileReader.readLine();
            while (line != null) {
                Payment importedPayment = parsePayment(line);
                this.savePayment(importedPayment);
                counter++;
                line = fileReader.readLine();
            }
            System.out.println("Imported " + counter + " payments");
        } catch (IOException e) {
            System.out.println("I/O Error occurred during import!");
            return false;
        } catch (IllegalArgumentException iae) {
            System.out.println("Payment parsing error occurred during import!");
            return false;
        }
        return true;
    }

    /**
     * Method for exporting tracker Report. Both history and balance.
     * Plain txt file will be created in directory from which app is launched.
     *
     * @param fileName as String to write to.
     * @return true if successful, false if otherwise.
     */
    public boolean exportPayments(String fileName) {
        final BufferedWriter fileWriter;

        try {
            fileWriter = FileIO.getFileWriter(fileName);
            final String history = Messages.history(this.dataContainer.getData());
            final String balance = Messages.balance(this.getBalance());

            fileWriter.write(history);
            fileWriter.write(balance);
            fileWriter.flush();
            fileWriter.close();
            System.out.println("Payments exported successfully to: " + fileName);
        } catch (IOException e) {
            System.out.println("I/O Error occurred during import!");
            return false;
        } catch (IllegalArgumentException iae) {
            System.out.println("Payment parsing error occurred during import!");
            return false;
        }
        return true;
    }

    public Collection<Payment> getAllPayments() {
        return this.dataContainer.getData();
    }

    public Payment getPaymentById(Long id) {
        final Optional<Payment> promise = this.dataContainer.getData()
                .stream()
                .filter(payment -> payment.getId().equals(id))
                .findFirst();

        if (promise.isPresent()) {
            return promise.get();
        } else {
            System.out.println("payment with id: " + id + " not found");
            return null;
        }
    }

    public Payment getPaymentById(String commandBody) {
        try {
            final Long id = Long.valueOf(commandBody.trim());
            return this.getPaymentById(id);
        } catch (NumberFormatException ex) {
            System.out.println("Unable to parse id: " + commandBody);
            return null;
        }
    }

    public Boolean removePayment(Long id) {
        final Payment payment2Remove = this.getPaymentById(id);
        Boolean result;
        if (payment2Remove != null) {
            result = this.dataContainer.getData().remove(payment2Remove);
        } else {
            return false;
        }

        if (result) {
            System.out.println("Payment with id: " + id + " removed");
        } else {
            System.out.println("Unable to remove Payment with id: " + id);
        }
        return result;
    }

    public Boolean removePayment(String commandBody) {
        try {
            final Long id = Long.valueOf(commandBody.trim());
            return this.removePayment(id);
        } catch (NumberFormatException ex) {
            System.out.println("Unable to parse id: " + commandBody);
            return null;
        }
    }

    public Collection<Payment> getPaymentsByCurrencyCode(String currencyCode) {
        return this.dataContainer.getData().stream().filter(
                payment -> payment.getCurrencyCode().equals(currencyCode)
        ).collect(Collectors.toList());
    }

    /**
     * This method returns calculated balance from all payment records.
     * Currencies, that have total amount = 0 are omitted.
     *
     * @see cz.majlon.bsc.payment.domain.Balance
     * @return Collection of calculated Balance
     */
    public Collection<Balance> getBalance() {
        final List<Balance> result = new ArrayList<>();
        final Map<String, BigDecimal> runningTotal = new HashMap<>();

        this.dataContainer.getData().forEach(payment -> {
            final String code = payment.getCurrencyCode();
            if (runningTotal.containsKey(code)) {
                final BigDecimal previousValue = runningTotal.get(code);
                final BigDecimal newValue = previousValue.add(payment.getAmount());
                runningTotal.put(code, newValue);
            } else {
                runningTotal.put(code, payment.getAmount());
            }
        });

        for (Map.Entry<String, BigDecimal> eachTotal : runningTotal.entrySet()) {
            result.add(new Balance(
                    eachTotal.getKey(),
                    eachTotal.getValue(),
                    this.dataContainer.getExchangeRate()
                            .get(eachTotal.getKey())));
        }

        final List<Balance> zeroCurrencies = result.stream()
                .filter(balance -> balance.getAmount().equals(new BigDecimal(0)))
                .collect(Collectors.toList());

        result.removeAll(zeroCurrencies);
        return result;
    }

    public Boolean saveExchangeRate(String commandBody) {
        final String trimmed = commandBody.trim().toLowerCase();
        final String[] parts = trimmed.split("\\s");

        if (parts.length == 2) {
            this.dataContainer.getExchangeRate().put(
                    parseCurrencyCode(parts[0]),
                    parseAmount(parts[1]).doubleValue()
            );
            System.out.println("Exchange rate saved: " + commandBody);
            return true;
        } else {
            System.out.println("Invalid command body parts!");
            return false;
        }
    }

    public int getRecordCount() {
        return this.dataContainer.getData().size();
    }

    /**
     * Parses Payment from input
     *
     * @param input String from console or other possible source (file etc.)
     * @return parsed Payment
     * @throws IllegalArgumentException if invalid arguments are provided.
     */
    private static Payment parsePayment(String input) {
        String trimmed = input.trim().toLowerCase();
        String[] parts = trimmed.split("\\s");

        if (parts.length == 2) {
            return new Payment(
                    parseCurrencyCode(parts[0]),
                    parseAmount(parts[1])
            );
        } else {
            throw new IllegalArgumentException("Invalid commandBody parts!");
        }
    }

    private static String parseCurrencyCode(String input) throws IllegalArgumentException {
        if (input.length() != 3) {
            throw new IllegalArgumentException("Invalid length of currency code!");
        }
        return input.toUpperCase();
    }

    private static BigDecimal parseAmount(String input) throws NumberFormatException {
        return new BigDecimal(input);
    }

}
