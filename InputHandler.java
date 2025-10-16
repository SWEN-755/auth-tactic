import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;


// gathers input for JAAS framework
public class InputHandler implements CallbackHandler {
    private final String username;
    private final char[] password;

    public InputHandler(String username, String password) {
        this.username = username;
        this.password = password.toCharArray();
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                ((NameCallback) callback).setName(username);
            } else if (callback instanceof PasswordCallback) {
                ((PasswordCallback) callback).setPassword(password);
            } else {
                throw new UnsupportedCallbackException(callback, "Unsupported callback type.");
            }
        }
    }
}