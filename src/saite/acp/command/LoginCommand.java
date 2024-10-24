package saite.acp.command;

import saite.acp.server.Context;
import saite.acp.user.User;
import saite.acp.user.UserID;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class LoginCommand extends Command {
    private UserID userID;
    private String rawPassword;

    public LoginCommand(Context context, String rawUserID, String rawPassword) throws CommandException {
        super(context);

        this.userID = new UserID(rawUserID);
        this.rawPassword = rawPassword;
    }

    @Override
    public void execute() throws CommandException {
        HashMap<UserID, User> userMap = getContext().getServer().getUsers();
        HashMap<UserID, User> loggedUsers = getContext().getServer().getLoggedUsers();

        User user = userMap.get(this.userID);

        if (user == null) {
            throw new CommandException("User does not exist");
        }

        if (loggedUsers.containsKey(this.userID)) {
            throw new CommandException(String.format("%s is online", this.userID.getRawID()));
        }

        byte[] salt = user.getSalt();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(this.rawPassword.getBytes(StandardCharsets.UTF_8));
            md.update(salt);

            byte[] providedDigest = md.digest();

            if (!(Arrays.equals(providedDigest, user.getPasswordDigestWithSalt()))) {
                throw new WrongPasswordException();
            }
        } catch (NoSuchAlgorithmException e) {
            throw new CommandException("Environment does not support SHA-256: " + e);
        }

        this.getContext().setCurrentUser(user);
        this.getContext().getServer().userLogin(user);
        this.getContext().getServer().addObserver(this.getContext());

        System.out.printf("Welcome to ACP, %s\n", user.getUserID());
    }
}
