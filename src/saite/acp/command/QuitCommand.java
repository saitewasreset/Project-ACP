package saite.acp.command;

import saite.acp.server.Context;

public class QuitCommand extends Command {
    public QuitCommand(Context context) {
        super(context);
    }

    @Override
    public void execute() {
        this.getContext().getServer().shutdown();

        System.exit(0);
    }
}
