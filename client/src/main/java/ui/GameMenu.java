package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.*;
import client.websocket.WebSocketFacade;
import exception.ResponseException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public abstract class GameMenu extends UIMenu {
    final String authToken;
    final int gameID;
    final ChessGame.TeamColor teamColor;
    final WebSocketFacade webSocketFacade;
    final BoardDrawer boardDrawer = new BoardDrawer();

    public GameMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            WebSocketFacade webSocketFacade,
            String authToken,
            int gameID,
            ChessGame.TeamColor teamColor
    ) {
        super(engine, serverFacade);
        this.webSocketFacade = webSocketFacade;
        this.authToken = authToken;
        this.gameID = gameID;
        this.teamColor = teamColor;

        try {
            webSocketFacade.joinGame(authToken, gameID);
            engine.setWaiting(true);
        }
        catch (ResponseException ex) {
            alert(ex.getMessage());
        }
    }

    @Override
    public Optional<State> run() {
        if (engine.isWaiting()) {
            return Optional.empty();
        }

        return super.run();
    }

    State draw() {
        boardDrawer.drawBoard(System.out, engine.getGame().getBoard(), teamColor);
        return null;
    }


    State highlightMoves(String... params) throws ResponseException {
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

        return null;
    }

    private Collection<ChessPosition> extractEndPositions(Collection<ChessMove> moves) {
        Collection<ChessPosition> endPositions = new HashSet<>();

        for (ChessMove move : moves) {
            endPositions.add(move.getEndPosition());
        }

        return endPositions;
    }

    ChessPosition convertCoordinates(String position) throws ResponseException {
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

    State exit() throws ResponseException {
        notify("Exiting...\n");

        webSocketFacade.leaveGame(authToken, gameID);
        deleteMenuInstance();

        return State.SIGNED_IN;
    }

    abstract void deleteMenuInstance();
}
