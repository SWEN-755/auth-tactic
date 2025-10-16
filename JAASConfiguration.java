import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.util.HashMap;
import java.util.Map;

public class JAASConfiguration extends Configuration {
    private final AppConfigurationEntry[] entries;
    public JAASConfiguration() {
        Map<String, String> options = new HashMap<>();
        this.entries = new AppConfigurationEntry[] {
                new AppConfigurationEntry(
                        LoginAuthentication.class.getName(),
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                        options
                )
        };
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        if ("AppEntry".equals(name)) {
            return entries;
        }
        return null;
    }
}