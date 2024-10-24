import saite.acp.command.Command;
import saite.acp.command.CommandException;
import saite.acp.server.Context;
import saite.acp.server.Server;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Server server = new Server();
        Context context = server.getContext();

        Scanner input = new Scanner(System.in);

        while (input.hasNextLine()) {
            String line = input.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }
            try {
                Command.parse(context, line).execute();
            } catch (CommandException e) {
                System.out.println(e);
            }

        }
    }
}
