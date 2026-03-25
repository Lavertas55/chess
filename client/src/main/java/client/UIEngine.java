package client;

import chess.ChessGame;

import java.util.HashMap;

public interface UIEngine {
    void setAuthToken(String authToken);
    void setTeamColor(ChessGame.TeamColor teamColor);
    void setGames(HashMap<Integer, Integer> games);
}
