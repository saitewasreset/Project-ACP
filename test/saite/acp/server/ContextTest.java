package saite.acp.server;

import org.junit.jupiter.api.Test;
import saite.acp.user.User;
import saite.acp.user.UserID;
import saite.acp.user.UserRole;

import static org.junit.jupiter.api.Assertions.*;

class ContextTest {

    @Test
    void testEquals() {
        User user = new User(new UserID("23371001"), "saitewasreset", new byte[]{}, new byte[]{}, UserRole.Student);
        User user2 = new User(new UserID("23371002"), "saitewasreset", new byte[]{}, new byte[]{}, UserRole.Student);
        Server server = new Server();

        Context context1 = new Context(server);
        Context context2 = new Context(server);
        Context context3 = new Context(server);

        context1.setCurrentUser(user);
        context2.setCurrentUser(user);
        context3.setCurrentUser(user2);

        assertEquals(context1, context2);
        assertNotEquals(context1, context3);

    }
}