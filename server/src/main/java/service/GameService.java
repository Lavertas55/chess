package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.exception.BadDataException;
import dataaccess.exception.DataException;
import dataaccess.exception.DataNotFoundException;
import exception.ResponseException;
import model.GameData;
import response.ListGamesResponse;

import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public int createGame(String gameName) throws ResponseException {
        GameData gameData;

        try {
            gameData = gameDAO.createGame(gameName);
        }
        catch (BadDataException e) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Bad Request");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }

        return gameData.gameID();
    }

    public ListGamesResponse listGames() throws ResponseException {
        throw new RuntimeException("not implemented");
    }

    public void joinGame(ChessGame.TeamColor color, String username, int gameID) throws ResponseException {
        throw new RuntimeException("not implemented");
    }

    public void clearGames() {
        gameDAO.clear();
    }
}
