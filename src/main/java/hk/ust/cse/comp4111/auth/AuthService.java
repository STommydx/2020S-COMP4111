package hk.ust.cse.comp4111.auth;

import hk.ust.cse.comp4111.database.ConnectionManager;
import hk.ust.cse.comp4111.database.DatabaseUser;
import hk.ust.cse.comp4111.exception.BadCredentialsException;
import hk.ust.cse.comp4111.exception.DuplicateLoginException;
import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.exception.TokenNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
    private static final AuthService instance = new AuthService();

    private final Map<UUID, String> tokenList;
    private final Map<String, UUID> userList;

    private AuthService() {
        tokenList = new ConcurrentHashMap<>();
        userList = new ConcurrentHashMap<>();
    }

    public static AuthService getInstance() {
        return instance;
    }

    public String login(@NotNull LoginRequest request) throws InternalServerException, BadCredentialsException, DuplicateLoginException {
        String username = request.getUsername();
        String password = request.getPassword();
        try (ConnectionManager.ConnectionInstance connectionInstance = ConnectionManager.getInstance().getConnectionInstance()) {
            Connection connection = connectionInstance.getConnection();
            boolean loginResult = DatabaseUser.authenticate(connection, username, password);
            if (!loginResult) throw new BadCredentialsException(username);
        } catch (SQLException | InterruptedException e) {
            throw new InternalServerException(e);
        }
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        synchronized (this) {
            if (userList.containsKey(username)) throw new DuplicateLoginException(username);
            tokenList.put(uuid, username);
            userList.put(username, uuid);
        }
        return token;
    }

    public String getUser(@NotNull String token, boolean logout) throws TokenNotFoundException {
        UUID uuid;
        try {
            uuid = UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new TokenNotFoundException(token);
        }
        return getUser(uuid, logout);
    }

    public String getUser(@NotNull UUID uuid, boolean logout) throws TokenNotFoundException {
        String user;
        synchronized (this) {
            user = tokenList.get(uuid);
            if (logout && user != null) {
                tokenList.remove(uuid);
                userList.remove(user);
            }
        }
        if (user == null) throw new TokenNotFoundException(uuid.toString());
        return user;
    }

    public String logout(@NotNull String token) throws TokenNotFoundException {
        return getUser(token, true);
    }

    public String getUser(@NotNull String token) throws TokenNotFoundException {
        return getUser(token, false);
    }

}
