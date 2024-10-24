package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import saite.acp.server.Context;
import saite.acp.server.Server;

import static org.junit.jupiter.api.Assertions.*;

class LogoutCommandTest {
    private Server server;
    private Context context;

    @BeforeEach
    void prepareContext() {
        this.server = new Server();
        this.context = server.getContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"logout 23371058 XXXX"})
    void illegalArgumentCount(String command) {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, command).execute());

        assertEquals(e.toString(), "Illegal argument count");
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

        @Test
        void notLogin() {
            NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "logout").execute());
            assertEquals(e.toString(), "No one is online");
        }

        @Test
        void notLoginPrivileged() {
            NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "logout 23371001").execute());
            assertEquals(e.toString(), "No one is online");
        }


        @Nested
        class WithSingleLoginUnPrivileged {
            @BeforeEach
            void prepareLogin() {
                Command.parse(context, "login 23371001 AAA111@@@").execute();
            }

            @Test
            void simpleSuccess() {
                Command.parse(context, "logout").execute();
            }

            @Test
            void alreadyLogout() {
                Command.parse(context, "logout").execute();
                NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "logout").execute());
                assertEquals(e.toString(), "No one is online");
            }

            @Test
            void permissionDenied() {
                PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "logout 23371001").execute());
                assertEquals(e.toString(), "Permission denied");
            }

            @Test
            void priority() {
                // Permission denied -> Illegal user id
                PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "logout 12345").execute());
                assertEquals(e.toString(), "Permission denied");
            }
        }

        @Nested
        class WithSingleLoginPrivileged {
            @BeforeEach
            void prepareLogin() {
                Command.parse(context, "login AD123 AAA111@@@").execute();
                Command.parse(server.getContext(), "login 23371001 AAA111@@@").execute();
                Command.parse(server.getContext(), "login SY2221118 AAA111@@@").execute();
                Command.parse(server.getContext(), "login BY2221118 AAA111@@@").execute();
            }

            @Test
            void simpleSuccess() {
                Command.parse(context, "logout 23371001").execute();
            }

            @Test
            void userNotExist() {
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "logout 23371002").execute());
                assertEquals(e.toString(), "User does not exist");
            }

            @Test
            void alreadyLogout() {
                Command.parse(context, "logout 23371001").execute();
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "logout 23371001").execute());
                assertEquals(e.toString(), "23371001 is not online");
            }

            @Test
            void userNotOnline() {
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "logout 12345").execute());
                assertEquals(e.toString(), "12345 is not online");
            }

            @Test
            void priority() {
                // Illegal user id  -> User does not exist
                IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "logout XXXXX").execute());
                assertEquals(e.toString(), "Illegal user id");

                // User does not exist -> 19999 is not online
                CommandException e2 = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "logout 19999").execute());
                assertEquals("User does not exist", e2.toString());

            }
        }
    }

}