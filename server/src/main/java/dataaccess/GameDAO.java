package dataaccess;

import chess.ChessGame;
import dataaccess.exception.DataException;
import model.GameData;

import java.util.Collection;

public interface GameDAO extends DataAccessObject {
    GameData createGame(String gameName) throws DataException;
    GameData getGame(int gameID) throws DataException;
    Collection<GameData> listGames() throws DataException;
    void updateGameUser(int gameID, ChessGame.TeamColor teamColor, Integer userID) throws DataException;
    Integer getGameUser(int gameID, ChessGame.TeamColor teamColor) throws DataException;
    void updateGameString(int gameID, String gameString) throws DataException;
}
