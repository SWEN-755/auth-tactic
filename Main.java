import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class Main {
    // map to hold: URL -> required role
    private static final Map<String, String> SERVICE_POLICY = new HashMap<>();
    static {
        SERVICE_POLICY.put("/admin/system/shutdown", "admin");
        SERVICE_POLICY.put("/view/public/data", "user");
        SERVICE_POLICY.put("/api/secret/key", "superadmin");
    }

    // checks if user is authorized to view URL
    private static boolean isAuthorized(Subject subject, String requiredRole) {
        String lowerRequiredRole = requiredRole.toLowerCase();
        // check role(s) associated with the subject
        for (Principal p : subject.getPrincipals()) {
            if (p instanceof RoleAuthorization) {
                String roleName = p.getName().toLowerCase();
                // check if matches required role
                if (roleName.equals(lowerRequiredRole)) {
                    return true;
                }
            }
        }
        return false;
    }

    // simulates app service access attempt
    private static void accessService(Subject subject, String serviceUrl) {
        // 1. Determine the required role from the policy map
        String requiredRole = SERVICE_POLICY.get(serviceUrl);
        System.out.print("ATTEMPTING TO ACCESS " + serviceUrl + " (REQUIRES: " + requiredRole.toUpperCase() + ")");
        // 2. Perform the Authorization check
        if (isAuthorized(subject, requiredRole)) {
            System.out.println(": ACCESS GRANTED.");
        } else {
            System.out.println(": ACCESS DENIED.");
        }
    }

    public static void main(String[] args) {

        // --- STEP 1: Set the JAAS Configuration ---
        Configuration.setConfiguration(new JAASConfiguration());

        // --- STEP 2.a. AUTHENTICATION: LOGIN AS ADMIN ---
        Subject adminSubject = null;
        try {
            System.out.println("\n--- ATTEMPTING LOGIN AS ADMIN ---");
            LoginContext lc = new LoginContext("AppEntry", new InputHandler("admin", "adminpass"));
            lc.login();
            adminSubject = lc.getSubject();

            // --- STEP 2.b. AUTHORIZATION CHECKS FOR ADMIN ---
            System.out.println("\n--- AUTHORIZATION CHECK AS ADMIN ---");
            accessService(adminSubject, "/admin/system/shutdown"); //granted
            accessService(adminSubject, "/view/public/data"); //granted
            accessService(adminSubject, "/api/secret/key"); //denied
            lc.logout();
        } catch (LoginException e) {
            System.err.println("ADMIN LOGIN FAILED: " + e.getMessage());
        }

        // --- STEP 3.a. AUTHENTICATION: LOGIN AS REGULAR USER ---
        Subject userSubject = null;
        try {
            System.out.println("\n\n--- ATTEMPTING LOGIN AS REGULAR USER ---");
            LoginContext lc = new LoginContext("AppEntry", new InputHandler("user1", "userpass"));
            lc.login();
            userSubject = lc.getSubject();

            // --- STEP 3.b. AUTHORIZATION CHECKS FOR REGULAR USER ---
            System.out.println("\n--- AUTHORIZATION CHECK AS REGULAR USER ---");
            accessService(adminSubject, "/admin/system/shutdown"); //denied
            accessService(adminSubject, "/view/public/data"); //granted
            accessService(adminSubject, "/api/secret/key"); //denied
            lc.logout();
        } catch (LoginException e) {
            System.err.println("USER LOGIN FAILED: " + e.getMessage());
        }
    }
}