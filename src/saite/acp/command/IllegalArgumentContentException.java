package saite.acp.command;

public class IllegalArgumentContentException extends CommandException {
    private String argumentType;

    public IllegalArgumentContentException(String argumentType) {
        this.argumentType = argumentType;
    }

    @Override
    public String toString() {
        return String.format("Illegal %s", this.argumentType);
    }
}
