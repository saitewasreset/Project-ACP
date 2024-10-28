package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import saite.acp.server.Server;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {


    @Test
    void simpleNotFound() {
        CommandException ce = assertThrowsExactly(CommandNotFoundException.class, () -> {
            Command.parse(null, "RockAndStone!");
        });
        assertEquals("Command 'RockAndStone!' not found", ce.toString());
    }

    @Test
    void withFrontSpace() {
        Command result = Command.parse(null, "     \tquit");
        assertInstanceOf(QuitCommand.class, result);
    }

    @Test
    void withBackSpace() {
        Command result = Command.parse(null, "quit      \t");
        assertInstanceOf(QuitCommand.class, result);
    }

    @Test
    void withSpaceBetweenParameters() {
        Server s = new Server();
        Command result = Command.parse(s.getContext(), "register 23371058        saitewasreset   \t AAA111@@@ AAA111@@@ Administrator");
        assertInstanceOf(RegisterCommand.class, result);
    }

    @Test
    void quitCommand() {
        Command result = Command.parse(null, "quit");
        assertInstanceOf(QuitCommand.class, result);
    }

    @Test
    void quitCommandIllegalArgumentCount() {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> {
            Command.parse(null, "quit 23371058");
        });
    }
}