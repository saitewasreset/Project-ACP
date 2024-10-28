package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import saite.acp.server.Context;
import saite.acp.server.Server;

import static org.junit.jupiter.api.Assertions.*;

class PrintInfoCommandTest {
    private Context context;

    @BeforeEach
    void prepareContext() {
        this.context = new Server().getContext();
    }

    @Test
    void invalidArgumentCount() {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "printInfo 23371001 XXXX"));
        assertEquals(e.toString(), "Illegal argument count");
    }

    @Test
    void notLogin() {
        NoContextUserException e = assertThrowsExactly(NoContextUserException.class, () -> Command.parse(context, "printInfo").execute());
        assertEquals(e.toString(), "No one is online");
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
        class WithoutPrivilege {
            @BeforeEach
            void prepareLogin() {
                Command.parse(context, "login 23371001 AAA111@@@").execute();
            }

            @Test
            void permissionDenied() {
                PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "printInfo 23371001").execute());

                assertEquals("Permission denied", e.toString());
            }

            @Test
            void priority() {
                // Permission denied -> Illegal user id
                PermissionDeniedException e = assertThrowsExactly(PermissionDeniedException.class, () -> Command.parse(context, "printInfo XXXXXX").execute());

                assertEquals("Permission denied", e.toString());

            }

            @Test
            void simpleSuccess() {
                Command.parse(context, "printInfo").execute();
            }
        }

        @Nested
        class WithPrivilege {
            @BeforeEach
            void prepareLogin() {
                Command.parse(context, "login AD123 AAA111@@@").execute();
            }

            @Test
            void illegalUserID() {
                IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "printInfo 2337105").execute());

                assertEquals("Illegal user id", e.toString());
            }

            @Test
            void userDoesNotExist() {
                CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "printInfo 23371099").execute());

                assertEquals("User does not exist", e.toString());
            }

            @Test
            void simpleSuccessOne() {
                Command.parse(context, "printInfo").execute();
            }

            @Test
            void simpleSuccessTwo() {
                Command.parse(context, "printInfo 23371001").execute();
            }

            @Test
            void priority() {
                // Illegal user id -> User does not exist
                IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "printInfo 2337105").execute());

                assertEquals("Illegal user id", e.toString());
            }
        }
    }
}