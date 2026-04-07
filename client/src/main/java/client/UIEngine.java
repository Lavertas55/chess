package client;

import chess.ChessGame;

import java.util.HashMap;

public interface UIEngine {
    void setAuthToken(String authToken);
    void setTeamColor(ChessGame.TeamColor teamColor);
    ChessGame.TeamColor getTeamColor();
    void setGames(HashMap<Integer, Integer> games);
    void setGame(ChessGame game);
    ChessGame getGame();
    void setGameID(Integer gameID);
    void setWaiting(boolean state);
    boolean isWaiting();
}
