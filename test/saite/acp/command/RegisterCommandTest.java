package saite.acp.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import saite.acp.server.Context;
import saite.acp.server.Server;

import static org.junit.jupiter.api.Assertions.*;

class RegisterCommandTest {
    private Context context;

    @BeforeEach
    void prepareContext() {
        this.context = new Server().getContext();
    }


    @Test
    void simpleSuccess() {
        Command.parse(context, "register 23371058 saitewasreset AAA111@@@ AAA111@@@ Administrator").execute();

    }

    @Test
    void parameterWithSpace() {
        Command.parse(context, "register 23371058       saitewasreset AAA111@@@  \t AAA111@@@ Administrator").execute();
    }

    @Test
    void parameterWithLeftSpace() {
        Command.parse(context, "   \t  register 23371058 saitewasreset AAA111@@@ AAA111@@@ Administrator").execute();
    }

    @Test
    void parameterWithRightSpace() {
        Command.parse(context, "register 23371058 saitewasreset AAA111@@@ AAA111@@@ Administrator    \t ").execute();
    }

    @ParameterizedTest
    @ValueSource(strings = {"register 23371058 saitewasreset AAA111@@@ AAA111@@@ Administrator 23371058",
            "register 23371058 saitewasreset AAA111@@@ AAA111@@@", "register 23371058 saitewasreset AAA111@@@"})
    void illegalArgumentCount(String rawCommand) {
        IllegalArgumentCountException e = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, rawCommand).execute());
        assertEquals(e.toString(), "Illegal argument count");
    }

    @Test
    void illegalUserID() {
        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "register 2337105 saitewasreset AAA111@@@ AAA111@@@ Administrator").execute());
        assertEquals(e.toString(), "Illegal user id");
    }

    @Test
    void userExist() {
        Command.parse(context, "register 23371058 saitewasreset AAA111@@@ AAA111@@@ Administrator").execute();
        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "register 23371058 saitewasreset AAA111@@@ AAA111@@@ Administrator").execute());
        assertEquals(e.toString(), "User id exists");
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaaa", "a_aaa", "aaa_", "AAAA", "AaaA", "saitewasreset", "Rock_And_Stone", "a____"})
    void validUsername(String username) {
        Command.parse(context, String.format("register 23371058 %s AAA111@@@ AAA111@@@ Administrator", username)).execute();
    }

    @ParameterizedTest
    @ValueSource(strings = {"_aaa", "a", "aa", "aaa", "aaa\uD83E\uDD7A", "aaaaaaaaaaaaaaaaaa", "Rock_And_Stone!"})
    void illegalUsername(String username) {
        String command = String.format("register 23371058 %s AAA111@@@ AAA111@@@ Administrator", username);

        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, command).execute());

        assertEquals(e.toString(), "Illegal user name");
    }

    @ParameterizedTest
    @ValueSource(strings = {"A1@A1@", "A1@A1@A1@A1@", "A1@A1@A1@A1@A1@A", "Aa1@@@@@____"})
    void validPassword(String password) {
        Command.parse(context, String.format("register 23371058 saitewasreset %s %s Administrator", password, password)).execute();
    }

    @ParameterizedTest
    @ValueSource(strings = {"A1@", "A1@A1@A1@A1@A1@A1@", "AaAaAaAa", "111111", "@@@@@@", "11AaAa", "11@@@@", "1111AAAa", "111AAA@@@|||", "111AAA@@@{{{"})
    void illegalPassword(String password) {
        String command = String.format("register 23371058 saitewasreset %s %s Administrator", password, password);

        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, command).execute());

        assertEquals(e.toString(), "Illegal password");
    }

    @Test
    void mismatchPassword() {
        CommandException e = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "register 23371058 saitewasreset XXX111@@@ AAA111@@@ Administrator").execute());
        assertEquals(e.toString(), "Passwords do not match");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Administrator", "Teacher", "Student"})
    void validIdentity(String identity) {
        Command.parse(context, String.format("register 23371058 saitewasreset AAA111@@@ AAA111@@@ %s", identity)).execute();
    }

    @ParameterizedTest
    @ValueSource(strings = {"DeepRockGalactic", "Rock_And_Stone"})
    void invalidIdentity(String identity) {
        String command = String.format("register 23371058 saitewasreset AAA111@@@ AAA111@@@ %s", identity);

        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, command).execute());

        assertEquals(e.toString(), "Illegal identity");
    }

    @Test
    void priority() {
        // Illegal argument count -> Illegal user id

        IllegalArgumentCountException e1 = assertThrowsExactly(IllegalArgumentCountException.class, () -> Command.parse(context, "register 233710XX saitewasreset AAA111@@@ AAA111@@@").execute());
        assertEquals(e1.toString(), "Illegal argument count");

        // Illegal user id -> Illegal user name
        IllegalArgumentContentException e2 = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "register 233710XX a AAA111@@@ AAA111@@@ Administrator").execute());

        assertEquals(e2.toString(), "Illegal user id");

        // Illegal user name -> Illegal password
        IllegalArgumentContentException e3 = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "register 23371058 a A A Administrator").execute());

        assertEquals(e3.toString(), "Illegal user name");

        // Illegal password -> Password do not match
        IllegalArgumentContentException e4 = assertThrowsExactly(IllegalArgumentContentException.class, () -> Command.parse(context, "register 23371058 saitewasreset A B Administrator").execute());

        assertEquals(e4.toString(), "Illegal password");

        // Password do not match -> Illegal identity
        CommandException e5 = assertThrowsExactly(CommandException.class, () -> Command.parse(context, "register 23371058 saitewasreset AAA111@@@ AAA111___ Rock_And_Stone"));

        assertEquals(e5.toString(), "Passwords do not match");
    }
}