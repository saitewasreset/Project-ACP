package saite.acp.command;

import saite.acp.server.Context;
import saite.acp.user.UserID;
import saite.acp.user.UserRole;

public class LogoutCommand extends Command {
    private String rawUserID;

    public LogoutCommand(Context context, String rawUserID) {
        super(context);

        this.rawUserID = rawUserID;
    }

    @Override
    public void execute() throws CommandException {
        Context context = this.getContext();
        if (context.getCurrentUser() == null) {
            throw new NoContextUserException();
        }

        if (rawUserID == null) {
            context.getServer().userLogout(context.getCurrentUser().getUserID());
        } else {
            if (context.getCurrentUser().getUserRole() != UserRole.Administrator) {
                throw new PermissionDeniedException();
            }

            UserID userID = new UserID(rawUserID);

            if (!context.getServer().getUsers().containsKey(userID)) {
                throw new CommandException("User does not exist");
            }

            if (!context.getServer().getLoggedUsers().containsKey(userID)) {
                throw new CommandException(String.format("%s is not online", rawUserID));
            }

            context.getServer().userLogout(userID);


        }
    }
}
