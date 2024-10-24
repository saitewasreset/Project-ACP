package saite.acp.command;

public class CommandNotFoundException extends CommandException {
    private String command;
    public CommandNotFoundException(String command) {
        super(command);

        this.command = command;
    }

    @Override
    public String toString() {
        return String.format("Command '%s' not found", this.command);
    }
}
