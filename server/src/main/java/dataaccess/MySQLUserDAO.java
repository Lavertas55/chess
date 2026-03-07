package dataaccess;

import dataaccess.exception.*;
import model.UserData;
import request.RegisterRequest;

import java.sql.*;

public class MySQLUserDAO implements UserDAO {
    @Override
    public UserData createUser(RegisterRequest registerRequest) throws DataException {
        if (
                registerRequest == null ||
                registerRequest.username() == null ||
                registerRequest.password() == null ||
                registerRequest.email() == null
        ) {
            throw new BadDataException("registerRequest cannot be null");
        }

        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (
                    PreparedStatement preparedStatement =
                    connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)
            ) {
                String username = registerRequest.username();
                String hashedPassword = UserDAO.hashPassword(registerRequest.password());
                String email = registerRequest.email();

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, email);

                preparedStatement.executeUpdate();

                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int userID = resultSet.getInt(1);

                    return new UserData(userID, username, hashedPassword, email);
                }
            }
        }
        catch (SQLIntegrityConstraintViolationException e) {
            throw new DataConflictException(String.format("username is in use: %s", e.getMessage()));
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to update database: %s", e.getMessage()));
        }

        return null;
    }

    @Override
    public UserData getUser(int userID) throws DataException {
        String statement = "SELECT * FROM user WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, userID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String email = resultSet.getString("email");

                        return new UserData(userID, username, password, email);
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("failed to get user: %s", e.getMessage()));
        }

        throw new DataNotFoundException(String.format("userID %d not found", userID));
    }

    @Override
    public UserData getUser(String username) throws DataException {
        String statement = "SELECT * FROM user WHERE username = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userID = resultSet.getInt("id");
                        String password = resultSet.getString("password");
                        String email = resultSet.getString("email");

                        return new UserData(userID, username, password, email);
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("failed to get user: %s", e.getMessage()));
        }

        throw new DataNotFoundException(String.format("username %s not found", username));
    }

    @Override
    public void clear() throws DataException {
        String[] statements = {
                "DELETE FROM user",
                "ALTER TABLE user AUTO_INCREMENT = 1"
        };

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);

            for (String statement : statements) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
                catch (SQLException e) {
                    connection.rollback();
                    throw e;
                }
            }

            connection.commit();
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("failed to clear database: %s", e.getMessage()));
        }
    }
}
