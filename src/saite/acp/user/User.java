package saite.acp.user;

public class User {
    private UserID id;
    private String name;
    private byte[] passwordDigestWithSalt;
    private byte[] salt;
    private UserRole role;

    public User(UserID id, String name, byte[] passwordDigestWithSalt, byte[] salt, UserRole role) {
        this.id = id;
        this.name = name;
        this.passwordDigestWithSalt = passwordDigestWithSalt;
        this.salt = salt;
        this.role = role;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getPasswordDigestWithSalt() {
        return passwordDigestWithSalt;
    }

    public UserID getUserID() {
        return this.id;
    }

    public UserRole getUserRole() {
        return this.role;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("""
                User id: %s
                Name: %s
                Type: %s""", this.id, this.name, this.role);
    }
}
