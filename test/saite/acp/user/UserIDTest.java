package saite.acp.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import saite.acp.command.IllegalArgumentContentException;
import saite.acp.command.IllegalArgumentCountException;

import static org.junit.jupiter.api.Assertions.*;

class UserIDTest {

    @Test
    void checkUndergraduate() {
        new UserID("22375030");
    }

    @ParameterizedTest
    @ValueSource(strings = {"18375030", "25375030", "22005030", "22445030", "22370030", "22377030", "22375000", "Rock and stone!", "2237500000"})
    void checkUndergraduateInvalid(String rawUserID) {
        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> {
            new UserID(rawUserID);
        });

        assertEquals(e.toString(), "Illegal user id");
    }

    @Test
    void checkMaster() {
        new UserID("SY2417503");
        new UserID("ZY2417503");

    }

    @ParameterizedTest
    @ValueSource(strings = {"XY2417503", "SY2017503", "SY2517503", "SY2400503",
            "SY2444503", "SY2417003", "SY2417703", "SY2417500", "RockAndStone!", "SYAAAAAAA", "RockAndSt"})
    void checkMasterInvalid(String invalidUserID) {
        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> {
            new UserID(invalidUserID);
        });

        assertEquals(e.toString(), "Illegal user id");
    }

    @Test
    void checkDoctoral() {
        new UserID("BY2317106");
    }

    @ParameterizedTest
    @ValueSource(strings = {"BY1817106", "BY2517106", "BY2300106",
            "BY2344106", "BY2317006", "BY2317706", "BY2317100", "BYAAAAAAA", "RockAndSt"})
    void checkDoctoralInvalid(String invalidUserID) {
        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> {
            new UserID(invalidUserID);
        });

        assertEquals(e.toString(), "Illegal user id");
    }

    @Test
    void checkTeacher() {
        new UserID("23333");
        new UserID("00001");
        new UserID("99999");
    }

    @ParameterizedTest
    @ValueSource(strings = {"00000", "0000A", "RockA"})
    void checkTeacherInvalid(String invalidUserID) {
        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> {
            new UserID(invalidUserID);
        });

        assertEquals(e.toString(), "Illegal user id");
    }

    @Test
    void checkAdmin() {
        new UserID("AD001");
        new UserID("AD233");
        new UserID("AD999");
    }

    @ParameterizedTest
    @ValueSource(strings = {"AD000", "AD00A", "ADRoc", "AD@@@"})
    void checkAdminInvalid(String invalidUserID) {
        IllegalArgumentContentException e = assertThrowsExactly(IllegalArgumentContentException.class, () -> {
            new UserID(invalidUserID);
        });

        assertEquals(e.toString(), "Illegal user id");
    }

    @ParameterizedTest
    @ValueSource(strings = {"22375030", "SY2221118", "BY2317503"})
    void getRawID(String rawUserID) {
        UserID u = new UserID(rawUserID);

        assertEquals(rawUserID, u.getRawID());
    }

    @ParameterizedTest
    @ValueSource(strings = {"22375030", "SY2221118", "BY2317503"})
    void testToString(String rawUserID) {
        UserID u = new UserID(rawUserID);

        assertEquals(rawUserID, u.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"22375030", "SY2221118", "BY2317503"})
    void testEquals(String rawUserID) {
        UserID u1 = new UserID(rawUserID);
        UserID u2 = new UserID(rawUserID);

        assertEquals(u1, u2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"22375030|22375031", "SY2221118|SY2221116", "BY2317503|BY2417503"})
    void testNotEquals(String compoundUserID) {
        String[] rawUserIDList = compoundUserID.split("\\|");

        UserID u1 = new UserID(rawUserIDList[0]);
        UserID u2 = new UserID(rawUserIDList[1]);

        assertNotEquals(u1, u2);
    }
}