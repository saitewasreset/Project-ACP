package saite.acp.command;

import saite.acp.server.Context;
import saite.acp.user.User;
import saite.acp.user.UserID;
import saite.acp.user.UserRole;

public class PrintInfoCommand extends Command {
    private String rawUserID;

    public PrintInfoCommand(Context context, String rawUserID) {
        super(context);

        this.rawUserID = rawUserID;
    }

    @Override
    public void execute() throws CommandException {
        Context context = getContext();

        if (context.getCurrentUser() == null) {
            throw new NoContextUserException();
        }

        if (rawUserID == null) {
            System.out.println(context.getCurrentUser());
        } else {
            if (context.getCurrentUser().getUserRole() != UserRole.Administrator) {
                throw new PermissionDeniedException();
            }

            UserID targetUserID = new UserID(this.rawUserID);

            User targetUserInfo = context.getServer().getUsers().get(targetUserID);

            if (targetUserInfo == null) {
                throw new CommandException("User does not exist");
            } else {
                System.out.println(targetUserInfo);
            }
        }

        System.out.println("Print information success");
    }
}
