package saite.acp.server;

import saite.acp.user.User;
import saite.acp.util.Observer;

public class Context implements Observer {
    private User currentUser;
    private Server server;

    public Context() {
        this.currentUser = null;
        this.server = null;
    }

    public Context(Server server) {
        this.currentUser = null;
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void update() {
        if (this.currentUser != null) {
            if (!server.getLoggedUsers().containsKey(currentUser.getUserID())) {
                this.currentUser = null;
            }
        }
    }
}
