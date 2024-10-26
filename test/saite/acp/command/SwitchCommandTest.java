package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import saite.acp.server.Context;
import saite.acp.server.Server;

import static org.junit.jupiter.api.Assertions.*;

class SwitchCommandTest {
    private Context context;
    private Server server;

    @BeforeEach
    void prepareContext() {
        this.server = new Server();
        this.context = server.getContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"switch", "switch 23371001 23371001"})
    void invalidArgumentCount(String command) {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, command));
        assertEquals("Illegal argument count", e.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2337100A", "SY222111A", "BY222111A", "1234A", "AD12A", "00000"})
    void illegalUserID(String rawUserID) {

        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class,
                () -> Command.parse(context, String.format("switch %s", rawUserID)));

        assertEquals("Illegal user id", e.toString());
    }

    @Nested
    class WithUsers {
        @BeforeEach
        void prepareUsers() {
            Command.parse(context, "register 23371001 Scout AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register SY2221118 Engineer AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register BY2221118 Gunner AAA111@@@ AAA111@@@ Student").execute();
            Command.parse(context, "register 12345 Driller AAA111@@@ AAA111@@@ Teacher").execute();
            Command.parse(context, "register AD123 saitewasreset AAA111@@@ AAA111@@@ Administrator").execute();
        }

        @Nested
        class WithLogin {
            @BeforeEach
            void prepareLogin() {
                Command.parse(context, "login 23371001 AAA111@@@").execute();
                Command.parse(context, "login SY2221118 AAA111@@@").execute();
            }

            @Test
            void simpleSuccess() {
                Command.parse(context, "switch 23371001").execute();
                assertEquals("23371001", context.getCurrentUser().getUserID().toString());
            }

            @Test
            void switchToSelf() {
                Command.parse(context, "switch SY2221118").execute();
                assertEquals("SY2221118", context.getCurrentUser().getUserID().toString());
            }

            @Test
            void notLogin() {
                Context newContext = server.getContext();
                Command.parse(newContext, "switch 23371001").execute();
                assertEquals("23371001", newContext.getCurrentUser().getUserID().toString());
            }

            @Test
            void userDoesNotExist() {
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "switch 23371099").execute());
                assertEquals("User does not exist", e.toString());
            }

            @Test
            void userIsNotOnline() {
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "switch 12345").execute());
                assertEquals("12345 is not online", e.toString());
            }

            @Test
            void afterLogout() {
                Command.parse(context, "logout").execute();
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "switch SY2221118").execute());
                assertEquals("SY2221118 is not online", e.toString());
            }
        }

    }
}