package saite.acp.user;

import saite.acp.command.CommandException;
import saite.acp.command.IllegalArgumentContentException;

public enum UserRole {
    Administrator,
    Teacher,
    Student;

    public static UserRole fromRawRole(String rawRole) throws CommandException {
        return switch (rawRole) {
            case "Administrator" -> Administrator;
            case "Teacher" -> Teacher;
            case "Student" -> Student;
            default -> throw new IllegalArgumentContentException("identity");
        };
    }
}
