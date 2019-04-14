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

    public Payment savePayment(String commandBody) {
        try {
            Payment payment2Save = parsePayment(commandBody);
            return this.savePayment(payment2Save);
        } catch (IllegalArgumentException ex) {
            System.out.println("Unable to parse payment: " + commandBody);
            return null;
        }
    }

    public boolean importPayments(String fileName) {
        BufferedReader fileReader;
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
            e.printStackTrace();
        } catch (IllegalArgumentException iae) {
            System.out.println("sumtin wen wong");
            iae.printStackTrace();
        }
        return true;
    }

    public boolean exportPayments(String fileName) {
        BufferedWriter fileWriter;

        try {
            fileWriter = FileIO.getFileWriter(fileName);
            String history = Messages.history(this.dataContainer.getData());
            String balance = Messages.balance(this.getBalance());

            fileWriter.write(history);
            fileWriter.write(balance);
            fileWriter.flush();
            fileWriter.close();
            System.out.println("Exported  payments");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException iae) {
            System.out.println("sumtin wen wong");
            iae.printStackTrace();
        }
        return true;
    }

    public Collection<Payment> getAllPayments() {
        return this.dataContainer.getData();
    }

    public Payment getPaymentById(Long id) {
        Optional<Payment> promise = this.dataContainer.getData()
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
            Long id = Long.valueOf(commandBody.trim());
            return this.getPaymentById(id);
        } catch (NumberFormatException ex) {
            System.out.println("Unable to parse id: " + commandBody);
            return null;
        }
    }

    public Boolean removePayment(Long id) {
        Payment payment2Remove = this.getPaymentById(id);
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
            Long id = Long.valueOf(commandBody.trim());
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

    public Collection<Balance> getBalance() {
        List<Balance> result = new ArrayList<>();
        Map<String, BigDecimal> runningTotal = new HashMap<>();

        this.dataContainer.getData().forEach(payment -> {
            String code = payment.getCurrencyCode();
            if (runningTotal.containsKey(code)) {
                BigDecimal previousValue = runningTotal.get(code);
                BigDecimal newValue = previousValue.add(payment.getAmount());
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
        return result;
    }


    public Boolean saveExchangeRate(String commandBody) {
        String trimmed = commandBody.trim().toLowerCase();
        String[] parts = trimmed.split("\\s");

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
