package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import saite.acp.server.Context;
import saite.acp.server.Server;

import static org.junit.jupiter.api.Assertions.*;

class LoginCommandTest {
    private Context context;

    @BeforeEach
    void prepareContext() {
        this.context = new Server().getContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"login 23371058", "login 23371058 AAA111@@@ Rock_And_Stone"})
    void illegalArgumentCount(String command) {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, command).execute());

        assertEquals(e.toString(), "Illegal argument count");
    }

    @Test
    void illegalUserID() {
        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "login 2337105 saitewasreset").execute());

        assertEquals(e.toString(), "Illegal user id");
    }

    @Test
    void userDoesNotExist() {
        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "login 23371058 AAA111@@@").execute());

        assertEquals(e.toString(), "User does not exist");
    }

    @Nested
    class WithSingleUser {
        @BeforeEach
        void prepareUser() {
            Command.parse(context, "register 23371058 saitewasreset AAA111@@@ AAA111@@@ Administrator").execute();
        }

        @Test
        void simpleSuccess() {
            Command.parse(context, "login 23371058 AAA111@@@").execute();
        }

        @Test
        void alreadyLogin() {
            Command.parse(context, "login 23371058 AAA111@@@").execute();
            CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "login 23371058 AAA111@@@").execute());

            assertEquals(e.toString(), "23371058 is online");
        }

        @Test
        void wrongPassword() {
            WrongPasswordException e = assertThrowsExactly(WrongPasswordException.class, () -> Command.parse(context, "login 23371058 AAA111@@").execute());

            assertEquals(e.toString(), "Wrong password");
        }

        @Test
        void priority() {
            // Illegal argument count -> Illegal user id
            IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "login 2337105 saitewasreset AAA111@@@").execute());
            assertEquals(e.toString(), "Illegal argument count");

            // Illegal user id -> User does not exist
            IllegalArgumentContentException e2 = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "login 2337105 saitewasreset").execute());
            assertEquals(e2.toString(), "Illegal user id");

        }
    }

}