package saite.acp.command;

public class PermissionDeniedException extends CommandException {
    @Override
    public String toString() {
        return "Permission denied";
    }
}
