package cz.majlon.bsc.payment.ui;

import cz.majlon.bsc.payment.domain.Balance;
import cz.majlon.bsc.payment.service.TrackerService;
import cz.majlon.bsc.payment.utils.Messages;

import java.util.Collection;
import java.util.Date;

/**
 * Object for periodical balance notification. Every one minute
 * console will output balance report. If parent thread ends notifier
 * will terminate itself within at least one second.
 */
public class BalanceNotifier implements Runnable {

    final private TrackerService trackerService;
    final private Thread parent;

    private static final long SECOND = 1000L;
    private static final long MINUTE = 60 * SECOND;

    public BalanceNotifier(TrackerService trackerService, Thread parent) {
        this.trackerService = trackerService;
        this.parent = parent;
    }

    @Override
    public void run() {
        long lastExecuted = new Date().getTime();

        while (parent.isAlive()) {
            if (new Date().getTime() - lastExecuted <= MINUTE && parent.isAlive()) {
                try {
                    Thread.sleep(SECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("* Automatic Balance notification *");
                Collection<Balance> balance = trackerService.getBalance();
                System.out.println(Messages.balance(balance));
                lastExecuted = new Date().getTime();
            }
        }
    }
}
