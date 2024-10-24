package saite.acp.command;

public class CommandException extends RuntimeException {
    private String detail;

    public CommandException() {}
    public CommandException(String detail) {
        super(String.format("command exception: %s", detail));
        this.detail = detail;
    }

    @Override
    public String toString() {
        return this.detail;
    }
}
