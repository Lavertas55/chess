package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;
import response.CreateGameResponse;
import response.ListGamesResponse;

public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public CreateGameResponse createGame(GameData newGame) throws ResponseException {
        throw new RuntimeException("not implemented");
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
