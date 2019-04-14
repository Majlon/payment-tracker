package cz.majlon.bsc.payment.service;

import cz.majlon.bsc.payment.domain.Balance;
import cz.majlon.bsc.payment.domain.DataContainer;
import cz.majlon.bsc.payment.domain.Payment;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class TrackerServiceTest {

    private TrackerService trackerService;

    @Before
    public void setUp() {
        trackerService = new TrackerService(new DataContainer());
    }

    @Test
    public void shouldHandleConcurrentInput() throws InterruptedException {

        new Thread(() -> getTestPayments().forEach(payment -> trackerService.savePayment(payment))).start();
        new Thread(() -> getTestPayments().forEach(payment -> trackerService.savePayment(payment))).start();
        new Thread(() -> getTestPayments().forEach(payment -> trackerService.savePayment(payment))).start();
        new Thread(() -> getTestPayments().forEach(payment -> trackerService.savePayment(payment))).start();

        Thread.sleep(2000);
        assertEquals(32, trackerService.getRecordCount());
    }

    @Test
    public void shouldCorrectlyCalculateBalance() throws InterruptedException {
        new Thread(() -> getTestPayments().forEach(payment -> trackerService.savePayment(payment))).start();

        Thread.sleep(2000);

        Optional<Balance> promise = this.trackerService
                .getBalance()
                .stream()
                .filter(balance -> balance.getCurrencyCode().equals("CZK"))
                .findFirst();

        if (promise.isPresent()) {
            assertEquals(new BigDecimal(800), promise.get().getAmount());
        } else {
            throw new IllegalStateException("No CZK balance record found");
        }
    }

    private Collection<Payment> getTestPayments() {
        List<Payment> testPayments = new ArrayList<>();

        testPayments.add(new Payment("CZK", BigDecimal.valueOf(100)));
        testPayments.add(new Payment("CZK", BigDecimal.valueOf(213)));
        testPayments.add(new Payment("CZK", BigDecimal.valueOf(300)));
        testPayments.add(new Payment("CZK", BigDecimal.valueOf(-53)));
        testPayments.add(new Payment("CZK", BigDecimal.valueOf(130)));
        testPayments.add(new Payment("CZK", BigDecimal.valueOf(110)));
        testPayments.add(new Payment("USD", BigDecimal.valueOf(500)));
        testPayments.add(new Payment("USD", BigDecimal.valueOf(-100)));

        return testPayments;
    }
}
