package saite.acp.command;

import saite.acp.server.Context;
import saite.acp.user.*;
import saite.acp.util.Range;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;

public class RegisterCommand extends Command {
    private static final Range<Integer> nameLengthRange = new Range<Integer>(4, 16);
    private static final Range<Integer> passwordLengthRange = new Range<Integer>(6, 16);

    private final UserID userID;
    private final String name;
    private final byte[] passwordDigestWithSalt;
    private final byte[] salt;

    private final UserRole role;


    public RegisterCommand(Context context, String rawID, String name, String rawPassword, String confirmPassword, String rawRole)
            throws CommandException, NoSuchAlgorithmException {
        super(context);
        this.userID = new UserID(rawID);

        HashMap<UserID, User> userMap = context.getServer().getUsers();

        if (userMap.containsKey(this.userID)) {
            throw new CommandException("User id exists");
        }

        // check name

        if (!nameLengthRange.checkValue(name.length())) {
            throw new IllegalArgumentContentException("user name");
        }

        if (name.charAt(0) == '_') {
            throw new IllegalArgumentContentException("user name");
        }

        for (char ch : name.toCharArray()) {
            if (ch >= 'a' && ch <= 'z') {
                continue;
            }

            if (ch >= 'A' && ch <= 'Z') {
                continue;
            }

            if (ch == '_') {
                continue;
            }

            throw new IllegalArgumentContentException("user name");
        }

        this.name = name;

        // check password

        if (!passwordLengthRange.checkValue(rawPassword.length())) {
            throw new IllegalArgumentContentException("password");
        }

        boolean hasAlpha = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char ch : rawPassword.toCharArray()) {
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
                hasAlpha = true;
            } else if (ch >= '0' && ch <= '9') {
                hasNumber = true;
            } else if ((ch == '@') || (ch == '_') || (ch == '%') || (ch == '$')) {
                hasSpecial = true;
            } else {
                throw new IllegalArgumentContentException("password");
            }

        }

        if (!(hasAlpha && hasNumber && hasSpecial)) {
            throw new IllegalArgumentContentException("password");
        }

        if (!rawPassword.equals(confirmPassword)) {
            throw new CommandException("Passwords do not match");
        }

        // check identity

        this.role = UserRole.fromRawRole(rawRole);

        SecureRandom sc = new SecureRandom();
        byte[] salt = new byte[128];
        sc.nextBytes(salt);

        this.salt = salt;

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(rawPassword.getBytes(StandardCharsets.UTF_8));
        md.update(salt);

        this.passwordDigestWithSalt = md.digest();
    }

    @Override
    public void execute() {
        HashMap<UserID, User> userMap = this.getContext().getServer().getUsers();

        User newUser = switch (this.role) {
            case Administrator -> new User(this.userID, this.name, this.passwordDigestWithSalt, this.salt, this.role);
            case Teacher -> new Teacher(this.userID, this.name, this.passwordDigestWithSalt, this.salt);
            case Student -> new Student(this.userID, this.name, this.passwordDigestWithSalt, this.salt);
        };

        userMap.put(this.userID, newUser);

        System.out.println("Register success");
    }
}
