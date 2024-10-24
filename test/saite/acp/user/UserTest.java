package saite.acp.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testToString() {
        String template = "User id: %s\n" +
                "Name: %s\n" +
                "Type: Administrator";
        User u = new User(new UserID("23371058"), "saitewasreset", new byte[0], new byte[0], UserRole.Administrator);

        assertEquals(String.format(template, "23371058", "saitewasreset"), u.toString());
    }
}