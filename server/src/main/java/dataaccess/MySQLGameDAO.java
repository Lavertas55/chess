package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.exception.BadDataException;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.DataException;
import dataaccess.exception.DataNotFoundException;
import model.GameData;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;

public class MySQLGameDAO implements GameDAO {
    @Override
    public GameData createGame(String gameName) throws DataException {
        if (gameName == null) {
            throw new BadDataException("gameName cannot be null");
        }

        ChessBoard board = new ChessGame().getBoard();

        String statement = "INSERT INTO game (white_user_id, black_user_id, name, game_state)" +
                " VALUES (NULL, NULL, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement =
                         connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)
            ) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, board.toJson());

                preparedStatement.executeUpdate();

                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int gameID = resultSet.getInt(1);

                    return new GameData(gameID, null, null, gameName, board.toJson());
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s", e.getMessage()));
        }

        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataException {
        String statement = "SELECT * FROM game WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return readGame(resultSet);
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to query database: %s", e.getMessage()));
        }

        throw new DataNotFoundException("gameID not in use");
    }

    @Override
    public Collection<GameData> listGames() throws DataException {
        HashSet<GameData> gameList = new HashSet<>();

        String statement = "SELECT * FROM game";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        gameList.add(readGame(resultSet));
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to query database: %s", e.getMessage()));
        }

        return gameList;
    }

    private GameData readGame(ResultSet resultSet) throws SQLException {
        int gameID = resultSet.getInt("id");
        int whiteUserID = resultSet.getInt("white_user_id");
        int blackUserID = resultSet.getInt("black_user_id");
        String gameName = resultSet.getString("name");
        String gameString = resultSet.getString("game_state");

        return new GameData(
                gameID,
                whiteUserID != 0 ? whiteUserID : null,
                blackUserID != 0 ? blackUserID : null,
                gameName,
                gameString
        );
    }

    @Override
    public void updateGameUser(int gameID, ChessGame.TeamColor teamColor, Integer userID) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Integer getGameUser(int gameID, ChessGame.TeamColor teamColor) throws DataException {
        if (teamColor == null) {
            throw new BadDataException("teamColor cannot be null");
        }

        String statement = "SELECT * FROM game WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String columnName = String.format("%s_user_id", teamColor.toString().toLowerCase());

                        int userID = resultSet.getInt(columnName);
                        return userID != 0 ? userID : null;
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to query database: %s", e.getMessage()));
        }

        throw new DataNotFoundException("gameID not in use");
    }

    @Override
    public void updateGameString(int gameID, String gameString) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void clear() throws DataException {
        String statement = "TRUNCATE game";

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
