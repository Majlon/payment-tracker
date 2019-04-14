package cz.majlon.bsc.payment.ui;

import cz.majlon.bsc.payment.domain.Balance;
import cz.majlon.bsc.payment.service.TrackerService;
import cz.majlon.bsc.payment.utils.Messages;

import java.util.Collection;

public class BalanceNotifier implements Runnable {

    final private TrackerService trackerService;
    final private Thread parent;

    public BalanceNotifier(TrackerService trackerService, Thread parent) {
        this.trackerService = trackerService;
        this.parent = parent;
    }

    @Override
    public void run() {
        while (parent.isAlive()) {
            try {
                Thread.sleep(60000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("* Automatic Balance notification *");
            Collection<Balance> balance = trackerService.getBalance();
            System.out.println(Messages.balance(balance));
        }
    }
}
