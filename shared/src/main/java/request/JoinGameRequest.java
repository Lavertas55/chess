package request;

import chess.ChessGame;

public record JoinGameRequest(ChessGame.TeamColor color, int gameID) {
}
