package cz.majlon.bsc.payment.service;

import cz.majlon.bsc.payment.domain.Balance;
import cz.majlon.bsc.payment.domain.Payment;
import cz.majlon.bsc.payment.enumeration.Command;
import cz.majlon.bsc.payment.utils.Messages;

import java.math.BigDecimal;
import java.util.Collection;

public class CommandRouterService {

    final private TrackerService trackerService;

    public CommandRouterService(TrackerService trackerService) {
        this.trackerService = trackerService;
    }

    public boolean readCommand(String input) {
        Command command = Command.parseCommand(input);
        String commandBody = command.getCommandBody(input);

        switch (command) {
            case ADD: {
                addPayment(commandBody);
                break;
            }
            case GET: {
                getPayment(commandBody);
                break;
            }
            case REM: {
                removePayment(commandBody);
                break;
            }
            case RATE: {
                addExchangeRate(commandBody);
                break;
            }
            case BALANCE: {
                printBalance();
                break;
            }
            case HISTORY: {
                printHistory();
                break;
            }
            case HELP: {
                System.out.println(Messages.helpMessage());
                break;
            }
            case IMPORT: {
                importPayments(commandBody);
                break;
            }
            case EXPORT: {
                exportPayments(commandBody);
                break;
            }
            case NOT_RECOGNIZED: {
                System.out.println("Command not recognized! ");
                System.out.println("For help, use command >help<");
                break;
            }
            case ADD_TEST: {
                this.addTestData();
                break;
            }
            case QUIT: {
                System.out.println("Ending console reader! ");
                return false;
            }
            default: {
                throw new IllegalArgumentException("Unexpected command input");
            }
        }
        return true;
    }

    private void exportPayments(String commandBody) {
        trackerService.exportPayments(commandBody);
    }

    private void addExchangeRate(String commandBody) {
        trackerService.saveExchangeRate(commandBody);
    }

    private void addPayment(String commandBody) {
        trackerService.savePayment(commandBody);
    }

    private void removePayment(String commandBody) {
        trackerService.removePayment(commandBody);
    }

    private void getPayment(String commandBody) {
        Payment payment = trackerService.getPaymentById(commandBody);
        System.out.println(payment);
    }

    private void printBalance() {
        Collection<Balance> balance = trackerService.getBalance();
        System.out.println(Messages.balance(balance));
    }

    private void printHistory() {
        Collection<Payment> payments = trackerService.getAllPayments();
        System.out.println(Messages.history(payments));
    }

    private void importPayments(String fileName) {
        trackerService.importPayments(fileName);
    }

    private void addTestData() {
        System.out.println("Adding testing data");
        new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                Payment testPayment = new Payment("TST", new BigDecimal(i));
                trackerService.savePayment(testPayment);
            }
        }).start();

        new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                Payment testPayment = new Payment("TST2", new BigDecimal(i));
                trackerService.savePayment(testPayment);
            }
        }).start();

        new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                Payment testPayment = new Payment("TST3", new BigDecimal(i));
                trackerService.savePayment(testPayment);
            }
        }).start();

        new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                Payment testPayment = new Payment("TST4", new BigDecimal(i));
                trackerService.savePayment(testPayment);
            }
        }).start();
    }
}
