package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @AfterEach
    public void cleanup() {
        try {
            facade.clear();
        }
        catch (ResponseException ex) {
            fail(String.format("Failed to clear database: %s", ex.getMessage()));
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerValid() {
        var authData = assertDoesNotThrow(() -> facade.register(
                "player1",
                "password",
                "player1@email.com"
        ));

        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerNull() {
        ResponseException exception = assertThrows(ResponseException.class, () -> facade.register(
                null,
                null,
                null
        ));

        assertEquals(ResponseException.Code.BAD_REQUEST, exception.getCode());
    }

    @Test
    public void logoutValid() {
        var registerResponse = assertDoesNotThrow(() -> facade.register(
                "player1",
                "password",
                "player1@email.com"
        ));

        assertDoesNotThrow(() -> facade.logout(registerResponse.authToken()));
    }

    @Test
    public void logoutInvalid() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.logout("Bad authToken")
        );

        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }

    @Test
    public void loginValid() {
        var registerResponse = assertDoesNotThrow(() -> facade.register(
                "player1",
                "password",
                "player1@email.com"
        ));

        assertDoesNotThrow(() -> facade.logout(registerResponse.authToken()));
        var loginResponse = assertDoesNotThrow(() -> facade.login("player1", "password"));
        assertTrue(loginResponse.authToken().length() > 10);
    }

    @Test
    public void loginInvalid() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.login("bad username", "bad password")
        );

        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }

    @Test
    public void createGameValid() {
        var registerResponse = assertDoesNotThrow(() -> facade.register(
                "player1",
                "password",
                "player1@email.com"
        ));

        assertDoesNotThrow(() -> facade.createGame("game1", registerResponse.authToken()));
    }

    @Test
    public void createGameInvalid() {
        ResponseException exception = assertThrows(
                ResponseException.class,
                () -> facade.createGame("game1", "invalid auth")
        );

        assertEquals(ResponseException.Code.UNAUTHORIZED, exception.getCode());
    }
}
