package cz.majlon.bsc.payment;

import cz.majlon.bsc.payment.domain.DataContainer;
import cz.majlon.bsc.payment.ui.BalanceNotifier;
import cz.majlon.bsc.payment.ui.ConsoleInputReader;
import cz.majlon.bsc.payment.service.TrackerService;

public class PaymentTracker {

    private static DataContainer dataContainer = new DataContainer();

    public static void main(String[] args) {

        final TrackerService trackerService = new TrackerService(dataContainer);

        final ConsoleInputReader reader = new ConsoleInputReader(trackerService);
        Thread readerThread = new Thread(reader);
        readerThread.start();

        final BalanceNotifier notifier = new BalanceNotifier(trackerService, readerThread);
        new Thread(notifier).start();
    }
}
