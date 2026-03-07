package dataaccess;

import dataaccess.exception.DataAccessException;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    private static final String[] CREATE_TABLE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS user(
                id INT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                UNIQUE(username)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS session(
                auth_token VARCHAR(255) PRIMARY KEY NOT NULL,
                user_id INT NOT NULL,
                FOREIGN KEY(user_id) REFERENCES user(id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS game(
                id INT PRIMARY KEY AUTO_INCREMENT,
                white_user_id INT,
                black_user_id INT,
                name VARCHAR(255) NOT NULL,
                game_state VARCHAR(255) NOT NULL,
                FOREIGN KEY(white_user_id) REFERENCES user(id),
                FOREIGN KEY(black_user_id) REFERENCES user(id)
            )
            """
    };

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }

        DatabaseManager.createTables();
    }

    static private void createTables() throws DataAccessException {
        for (String statement : DatabaseManager.CREATE_TABLE_STATEMENTS) {

            try (var conn = DatabaseManager.getConnection()) {
                var preparedStatement = conn.prepareStatement(statement);
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new DataAccessException("failed to create tables", ex);
            }
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
