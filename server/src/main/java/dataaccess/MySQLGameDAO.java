package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.exception.BadDataException;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.DataException;
import model.GameData;

import java.sql.*;
import java.util.Collection;

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
        throw new RuntimeException("not implemented");
    }

    @Override
    public Collection<GameData> listGames() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void updateGameUser(int gameID, ChessGame.TeamColor teamColor, Integer userID) throws DataException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Integer getGameUser(int gameID, ChessGame.TeamColor teamColor) throws DataException {
        throw new RuntimeException("not implemented");
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
