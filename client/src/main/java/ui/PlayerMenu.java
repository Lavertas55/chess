package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import client.State;
import client.UIEngine;
import client.websocket.WebSocketFacade;
import exception.ResponseException;

import java.util.Collection;
import java.util.HashSet;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class PlayerMenu extends GameMenu {
    private static PlayerMenu playerMenuInstance = null;

    public static PlayerMenu getPlayerMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            WebSocketFacade webSocketFacade,
            String authToken,
            int gameID,
            ChessGame.TeamColor teamColor
    ) {
        if (
                playerMenuInstance == null ||
                !playerMenuInstance.authToken.equals(authToken) ||
                playerMenuInstance.gameID != gameID ||
                !playerMenuInstance.teamColor.equals(teamColor)
        ) {
            playerMenuInstance = new PlayerMenu(
                    engine,
                    serverFacade,
                    webSocketFacade,
                    authToken,
                    gameID,
                    teamColor
            );
        }

        return playerMenuInstance;
    }

    PlayerMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            WebSocketFacade webSocketFacade,
            String authToken,
            int gameID,
            ChessGame.TeamColor teamColor
    ) {
        super(engine, serverFacade, webSocketFacade, authToken, gameID, teamColor);
    }

    @Override
    State eval(String cmd, String... params) throws ResponseException {
        return switch (cmd) {
            case "move" -> makeMove(params);
            case "draw" -> draw();
            case "resign" -> resign();
            case "highlight" -> highlightMoves(params);
            case "exit" -> exit();
            default -> help();
        };
    }

    @Override
    void printPrompt() {
        System.out.printf(
                "\n%s[IN GAME] >>>%s ",
                RESET_TEXT_COLOR,
                SET_TEXT_COLOR_GREEN
        );
    }

    private State makeMove(String... params) throws ResponseException {
        ChessMove move = createChessMove(params);

        webSocketFacade.makeMove(authToken, gameID, move);
        engine.setWaiting(true);


        return State.IN_GAME;
    }

    private ChessMove createChessMove(String... params) throws ResponseException {
        if (params.length < 2 || params.length > 3) {
            throw new ResponseException(
                    ResponseException.Code.BAD_REQUEST,
                    "Expected: <START POSITION> <END POSITION> [PROMOTION TYPE]"
            );
        }

        ChessPosition startPosition = convertCoordinates(params[0]);
        ChessPosition endPosition = convertCoordinates(params[1]);
        ChessPiece.PieceType promotionType = null;

        if (params.length == 3) {
            promotionType = convertPromotionType(params[2]);
        }

        return new ChessMove(startPosition, endPosition, promotionType);
    }

    private ChessPosition convertCoordinates(String position) throws ResponseException {
        if (position.length() != 2) {
            throw new ResponseException(
                    ResponseException.Code.BAD_REQUEST,
                    String.format("%s is not a valid chess position", position)
            );
        }

        position = position.toLowerCase();

        char file = position.charAt(0);
        char rank = position.charAt(1);

        file = switch (file) {
            case 'a' -> '1';
            case 'b' -> '2';
            case 'c' -> '3';
            case 'd' -> '4';
            case 'e' -> '5';
            case 'f' -> '6';
            case 'g' -> '7';
            case 'h' -> '8';
            default -> throw new ResponseException(
                                ResponseException.Code.BAD_REQUEST,
                                String.format("%s is not a valid chess file (A-H)", file)
                        );
        };

        return new ChessPosition(Character.getNumericValue(rank), Character.getNumericValue(file));
    }

    private ChessPiece.PieceType convertPromotionType(String pieceType) throws ResponseException {
        pieceType = pieceType.toLowerCase();

        return switch (pieceType) {
            case "b", "bishop" -> ChessPiece.PieceType.BISHOP;
            case "n", "knight" -> ChessPiece.PieceType.KNIGHT;
            case "r", "rook" -> ChessPiece.PieceType.ROOK;
            case "q", "queen" -> ChessPiece.PieceType.QUEEN;
            default -> throw new ResponseException(
                    ResponseException.Code.BAD_REQUEST,
                    String.format("%s is not a valid promotion type [BISHOP, KNIGHT, ROOK, QUEEN]", pieceType)
            );
        };
    }

    private State resign() throws ResponseException {
        webSocketFacade.resign(authToken, gameID);

        return State.IN_GAME;
    }

    private State highlightMoves(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "Expected: <POSITION OF PIECE>");
        }

        ChessPosition position = convertCoordinates(params[0]);
        ChessGame game = engine.getGame();

        Collection<ChessMove> validMoves = game.validMoves(position);
        if (validMoves == null) {
            throw new ResponseException(ResponseException.Code.BAD_REQUEST, "You must select a space with a piece");
        }

        Collection<ChessPosition> spacesToHighlight = extractEndPositions(validMoves);

        boardDrawer.drawBoard(System.out, game.getBoard(), teamColor, position, spacesToHighlight);

        return State.IN_GAME;
    }

    private Collection<ChessPosition> extractEndPositions(Collection<ChessMove> moves) {
        Collection<ChessPosition> endPositions = new HashSet<>();

        for (ChessMove move : moves) {
            endPositions.add(move.getEndPosition());
        }

        return endPositions;
    }

    @Override
    State help() {
        notify(
        """
             
             USAGE:
             - move <START POSITION> <END POSITION> [PROMOTION TYPE] - Submit move (Promotion type is optional)
             - draw - Redraw board
             - resign - resign from current game
             - exit - Exit current game
             - help - Show available commands
             """
        );

        return State.IN_GAME;
    }

    @Override
    void deleteMenuInstance() {
        playerMenuInstance = null;
    }
}
