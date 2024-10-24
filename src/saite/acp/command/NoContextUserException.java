package saite.acp.command;

public class NoContextUserException extends CommandException {
    @Override
    public String toString() {
        return "No one is online";
    }
}
