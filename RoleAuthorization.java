import java.security.Principal;


public class RoleAuthorization implements Principal {
    private final String name;

    public RoleAuthorization(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "RoleAuthorization: " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof RoleAuthorization)) return false;
        RoleAuthorization other = (RoleAuthorization) o;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}