package saite.acp.command;

public class IllegalArgumentCountException extends CommandException{
    @Override
    public String toString() {
        return "Illegal argument count";
    }
}
