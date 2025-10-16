import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class LoginAuthentication implements LoginModule {
    private Subject subject;
    private CallbackHandler callbackHandler;
    private boolean succeeded = false;
    private String username;
    private Set<Principal> principals = new HashSet<>();

    // pre-defined users and roles for system
    private static final Map<String, String> USERS = new HashMap<>();
    private static final Map<String, String> ROLES = new HashMap<>();

    static {
        USERS.put("admin", "adminpass");
        ROLES.put("admin", "admin");

        USERS.put("user1", "userpass");
        ROLES.put("user1", "user");
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("No CallbackHandler provided.");
        }
        // request username and password from the InputHandler
        Callback[] callbacks = new Callback[]{
                new NameCallback("Username: "),
                new PasswordCallback("Password: ", false)
        };

        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback) callbacks[0]).getName();
            char[] passwordChars = ((PasswordCallback) callbacks[1]).getPassword();
            String password = (passwordChars == null) ? "" : new String(passwordChars);

            // Authentication check
            if (USERS.containsKey(username) && USERS.get(username).equals(password)) {
                System.out.println("AUTHENTICATION SUCCESSFUL FOR " + username);
                succeeded = true;
                return true;
            } else {
                succeeded = false;
                throw new LoginException("INVALID CREDENTIALS");
            }
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("LOGIN MODULE ERROR: " + e.getMessage());
        }
    }

    @Override
    public boolean commit() throws LoginException {
        if (!succeeded) {
            return false;
        }
        // associate roles with the authenticated subject
        String roleName = ROLES.get(username);
        if (roleName != null) {
            principals.add(new RoleAuthorization(roleName));
            System.out.println("   -> Committed Role: " + roleName);

            // hierarchy logic: grants lower roles explicitly
            if (roleName.equals("admin")) {
                principals.add(new RoleAuthorization("user"));
            }
            subject.getPrincipals().addAll(principals);
        }
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        if (!succeeded) return false;
        logout();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().removeAll(principals);
        principals.clear();
        succeeded = false;
        return true;
    }
}