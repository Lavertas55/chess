package dataaccess;

import dataaccess.exception.*;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import request.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private static UserData validUser;
    private static RegisterRequest registerRequest;

    private UserDAO getUserDAO(Class<? extends UserDAO> dbClass) throws DataException {
        UserDAO userDAO;
        if (dbClass.equals(MySQLUserDAO.class)) {
            DatabaseManager.createDatabase();
            userDAO = new MySQLUserDAO();
        }
        else {
            userDAO = new MemoryUserDAO();
        }

        userDAO.clear();
        return userDAO;
    }

    @BeforeAll
    static void init() {
        int userID = 1;
        String username = "valid";
        String password = "password";
        String hashedPassword = UserDAO.hashPassword(password);
        String email = "test@yahoo.com";

        validUser = new UserData(userID, username, hashedPassword, email);
        registerRequest = new RegisterRequest(username, password, email);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class, MySQLUserDAO.class})
    void createUserValid(Class<? extends UserDAO> dbClass) throws DataException {
        UserDAO userDAO = getUserDAO(dbClass);

        assertDoesNotThrow(() -> userDAO.createUser(registerRequest));

        userDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class, MySQLUserDAO.class})
    void createUserNull(Class<? extends UserDAO> dbClass) throws DataException {
        UserDAO userDAO = getUserDAO(dbClass);

        assertThrows(BadDataException.class, () -> userDAO.createUser(null));

        userDAO.clear();
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class, MySQLUserDAO.class})
    void createUserExisting(Class<? extends UserDAO> dbClass) throws DataException {
        UserDAO userDAO = getUserDAO(dbClass);

        assertDoesNotThrow(() -> userDAO.createUser(registerRequest));
        assertThrows(DataConflictException.class, () -> userDAO.createUser(registerRequest));

        userDAO.clear();
    }

    @Nested
    class GetUser {

        @ParameterizedTest
        @ValueSource(classes = {MemoryUserDAO.class, MySQLUserDAO.class})
        void fromUsernameValid(Class<? extends UserDAO> dbClass) throws DataException {
            UserDAO userDAO = getUserDAO(dbClass);

            assertDoesNotThrow(() -> userDAO.createUser(registerRequest));

            UserData result = userDAO.getUser(validUser.username());

            assertEquals(validUser.userID(), result.userID());
            assertEquals(validUser.username(), result.username());
            assert(UserDAO.isPasswordEqual(registerRequest.password(), result.password()));
            assertEquals(validUser.email(), result.email());

            userDAO.clear();
        }

        @ParameterizedTest
        @ValueSource(classes = {MemoryUserDAO.class, MySQLUserDAO.class})
        void fromUsernameNonExistent(Class<? extends UserDAO> dbClass) throws DataException {
            UserDAO userDAO = getUserDAO(dbClass);

            assertThrows(DataNotFoundException.class, () -> userDAO.getUser("non-existent"));

            userDAO.clear();
        }

        @ParameterizedTest
        @ValueSource(classes = {MemoryUserDAO.class, MySQLUserDAO.class})
        void fromUserIDValid(Class<? extends UserDAO> dbClass) throws DataException {
            UserDAO userDAO = getUserDAO(dbClass);

            assertDoesNotThrow(() -> userDAO.createUser(registerRequest));

            UserData result = userDAO.getUser(validUser.userID());

            assertEquals(validUser.userID(), result.userID());
            assertEquals(validUser.username(), result.username());
            assert(UserDAO.isPasswordEqual(registerRequest.password(), result.password()));
            assertEquals(validUser.email(), result.email());

            userDAO.clear();
        }

        @ParameterizedTest
        @ValueSource(classes = {MemoryUserDAO.class, MySQLUserDAO.class})
        void fromUserIDNonExistent(Class<? extends UserDAO> dbClass) throws DataException {
            UserDAO userDAO = getUserDAO(dbClass);

            assertThrows(DataNotFoundException.class, () -> userDAO.getUser(3));

            userDAO.clear();
        }

        @ParameterizedTest
        @ValueSource(classes = {MemoryUserDAO.class, MySQLUserDAO.class})
        void withNull(Class<? extends UserDAO> dbClass) throws DataException {
            UserDAO userDAO = getUserDAO(dbClass);

            assertThrows(DataNotFoundException.class, () -> userDAO.getUser(null));

            userDAO.clear();
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class, MySQLUserDAO.class})
    void clear(Class<? extends UserDAO> dbClass) throws DataException {
        UserDAO userDAO = getUserDAO(dbClass);

        assertDoesNotThrow(() -> userDAO.createUser(registerRequest));

        assertDoesNotThrow(userDAO::clear);
    }
}