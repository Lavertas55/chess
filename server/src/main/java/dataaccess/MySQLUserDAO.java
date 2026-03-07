package dataaccess;

import dataaccess.exception.BadDataException;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.DataConflictException;
import dataaccess.exception.DataException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;

import java.sql.*;

public class MySQLUserDAO implements UserDAO {
    @Override
    public UserData createUser(RegisterRequest registerRequest) throws DataException {
        if (registerRequest == null) {
            throw new BadDataException("registerRequest cannot be null");
        }

        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (
                    PreparedStatement preparedStatement =
                    connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)
            ) {
                String username = registerRequest.username();
                String hashedPassword = hashPassword(registerRequest.password());
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

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public UserData getUser(int UserID) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public UserData getUser(String username) throws DataException {
        throw new RuntimeException("not implemented");
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
