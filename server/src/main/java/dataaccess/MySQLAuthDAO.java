package dataaccess;

import dataaccess.exception.BadDataException;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.DataConflictException;
import dataaccess.exception.DataException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class MySQLAuthDAO implements AuthDAO {

    @Override
    public void createAuth(AuthData authData) throws DataException {
        if (authData == null) {
            throw new BadDataException("Username cannot be null");
        }

        String statement = "INSERT INTO session (user_id, auth_token) VALUES (?, ?);";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, authData.userID());
                preparedStatement.setString(2, authData.authToken());

                preparedStatement.executeUpdate();
            }
        }
        catch (SQLIntegrityConstraintViolationException e) {
            throw new DataConflictException(String.format("Unable to update database: %s", e.getMessage()));
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to update database: %s", e.getMessage()));
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteAuth(String authToken) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE session";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }

        catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to clear database: %s", e.getMessage()));
        }
    }
}
