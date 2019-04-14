package cz.majlon.bsc.payment.enumeration;

public enum Command {

    ADD("add"),
    GET("get"),
    REM("rem"),
    BALANCE("balance"),
    HISTORY("history"),
    IMPORT("import"),
    EXPORT("export"),
    RATE("rate"),
    QUIT("quit"),
    HELP("help"),
    NOT_RECOGNIZED("nr"),
    ADD_TEST("add_test");

    private String command;

    Command(String command) {
        this.command = command;
    }

    public static Command parseCommand(String input) {
        String prep = input.trim().toLowerCase();
        String[] parts = prep.split("\\s", 2);

        try {
            return Command.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException ex) {
            return NOT_RECOGNIZED;
        }
    }

    public String getCommandBody(String input) {
        if (!this.command.equals(NOT_RECOGNIZED.command)) {
            return input.replaceFirst(command, "");
        } else {
            return NOT_RECOGNIZED.command;
        }
    }
}
