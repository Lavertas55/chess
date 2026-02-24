package response;

import chess.ChessGame;

import java.util.Collection;

public record ListGamesResponse(Collection<ChessGame> games) {
}
