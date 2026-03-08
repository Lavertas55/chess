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
        Collection<GameData> games;

        try {
           games = gameDAO.listGames();
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }

        return new ListGamesResponse(games);
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

    public void clearGames() throws ResponseException {
        try {
            gameDAO.clear();
        }
        catch (DataException e) {
            throw new ResponseException(ResponseException.Code.SERVER_ERROR, e.getMessage());
        }
    }
}
