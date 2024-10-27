package saite.acp.command;

import saite.acp.server.Context;
import saite.acp.user.User;
import saite.acp.user.UserID;

import java.util.HashMap;

public class SwitchCommand extends Command {
    private UserID userID;

    public SwitchCommand(Context context, String rawUserID) {
        super(context);
        this.userID = new UserID(rawUserID);
    }

    @Override
    public void execute() throws CommandException {
        HashMap<UserID, User> userMap = getContext().getServer().getUsers();
        HashMap<UserID, User> loggedUserMap = getContext().getServer().getLoggedUsers();

        if (!userMap.containsKey(this.userID)) {
            throw new CommandException("User does not exist");
        }

        User targetUser = loggedUserMap.get(this.userID);

        if (targetUser == null) {
            throw new CommandException(String.format("%s is not online", this.userID));
        }

        getContext().setCurrentUser(targetUser);

        System.out.printf("Switch to %s\n", this.userID);
    }
}
