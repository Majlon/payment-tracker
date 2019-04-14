package cz.majlon.bsc.payment.ui;

import cz.majlon.bsc.payment.service.CommandRouterService;
import cz.majlon.bsc.payment.service.TrackerService;
import cz.majlon.bsc.payment.utils.Messages;

import java.util.Scanner;

/**
 * Object for continuous reading user input from console.
 * Recognized commands are then send for processing via @CommandRouterService.
 */
public class ConsoleInputReader implements Runnable {

    final private CommandRouterService router;

    public ConsoleInputReader(TrackerService trackerService) {
        this.router = new CommandRouterService(trackerService);
    }

    @Override
    public void run() {
        System.out.println(Messages.welcomeMessage());

        final Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            final String line = in.nextLine().trim().toLowerCase();

            if (!router.readCommand(line)) {
                break;
            }
        }
    }
}
