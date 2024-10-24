package saite.acp.command;

public class WrongPasswordException extends CommandException {
    @Override
    public String toString() {
        return "Wrong password";
    }
}
