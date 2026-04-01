package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.exception.BadDataException;
import dataaccess.exception.DataException;
import dataaccess.exception.DataNotFoundException;
import exception.ResponseException;
import model.GameData;

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

    public Collection<GameData> listGames() throws ResponseException {
        try {
           return gameDAO.listGames();
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws ResponseException {
        try {
            return gameDAO.getGame(gameID);
        }
        catch (DataNotFoundException ex) {
            throw new ResponseException(
                    ResponseException.Code.NOT_FOUND,
                    String.format("Game ID %d does not exist", gameID)
            );
        }
        catch (DataException ex) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, ex.getMessage());
        }
    }

    public void joinGame(int gameID, ChessGame.TeamColor color, int userID) throws ResponseException {
        try {
            if (gameDAO.getGameUser(gameID, color) != null){
                throw new ResponseException(ResponseException.Code.FORBIDDEN, "Forbidden");
            }

            gameDAO.updateGameUser(gameID, color, userID);
        }
        catch (BadDataException | DataNotFoundException e) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Bad Request");
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }

    public void updateGame(int gameID, String gameString) throws ResponseException {
        try {
            gameDAO.updateGameString(gameID, gameString);
        }
        catch (DataNotFoundException ex) {
            throw new ResponseException(ResponseException.Code.NOT_FOUND, String.format("Game %d not found", gameID));
        }
        catch (DataException ex) {
            throw new ResponseException(
                    ResponseException.Code.SERVER_ERROR,
                    String.format("Failed to update game string: %s", ex.getMessage())
            );
        }
    }

    public void clearGames() throws ResponseException {
        try {
            gameDAO.clear();
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }
}
