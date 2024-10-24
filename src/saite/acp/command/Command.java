package saite.acp.command;

import saite.acp.server.Context;

import java.security.NoSuchAlgorithmException;

public abstract class Command {
    protected Context context;

    public Command(Context context) {
        this.context = context;
    }

    public static Command parse(Context context, String input) throws CommandException {
        input = input.strip();

        String[] args = input.split("\\s+");

        if (args.length < 1) {
            throw new CommandNotFoundException(input);
        }

        String commandName = args[0];

        // 都需要检查参数数量是否合法！

        return switch (commandName) {
            case "quit" -> {
                if (args.length != 1) {
                    throw new IllegalArgumentCountException();
                }

                yield new QuitCommand(context);
            }
            case "register" -> {
                if (args.length != 6) {
                    throw new IllegalArgumentCountException();
                }

                try {
                    yield new RegisterCommand(context, args[1], args[2], args[3], args[4], args[5]);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
            case "login" -> {
                if (args.length != 3) {
                    throw new IllegalArgumentCountException();
                }

                yield new LoginCommand(context, args[1], args[2]);
            }
            case "logout" -> {
                if (args.length > 2) {
                    throw new IllegalArgumentCountException();
                } else if (args.length == 1) {
                    yield new LogoutCommand(context, null);
                } else {
                    yield new LogoutCommand(context, args[1]);
                }
            }
            case "printInfo" -> {
                if (args.length > 2) {
                    throw new IllegalArgumentCountException();
                } else if (args.length == 1) {
                    yield new PrintInfoCommand(context, null);
                } else {
                    yield new PrintInfoCommand(context, args[1]);
                }
            }
            case "createCourse" -> {
                if (args.length != 5) {
                    throw new IllegalArgumentCountException();
                }

                yield new CreateCourseCommand(context, args[1], args[2], args[3], args[4]);
            }
            case "listCourse" -> {
                if (args.length > 2) {
                    throw new IllegalArgumentCountException();
                } else if (args.length == 1) {
                    yield new ListCourseCommand(context, null);
                } else {
                    yield new ListCourseCommand(context, args[1]);
                }
            }
            case "selectCourse" -> {
                if (args.length != 2) {
                    throw new IllegalArgumentCountException();
                } else {
                    yield new SelectCourseCommand(context, args[1]);
                }
            }
            case "cancelCourse" -> {
                if (args.length != 2) {
                    throw new IllegalArgumentCountException();
                } else {
                    yield new CancelCourseCommand(context, args[1]);
                }
            }
            default -> throw new CommandNotFoundException(commandName);
        };
    }

    public Context getContext() {
        return this.context;
    }

    public abstract void execute() throws CommandException;
}
