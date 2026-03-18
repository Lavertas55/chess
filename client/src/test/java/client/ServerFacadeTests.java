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

}
