package ui;

import client.ServerFacade;
import client.State;
import client.UIEngine;
import exception.ResponseException;

import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public abstract class UIMenu implements Displayable {
    final UIEngine engine;
    final ServerFacade serverFacade;

    public UIMenu(UIEngine engine, ServerFacade serverFacade) {
        this.engine = engine;
        this.serverFacade = serverFacade;
    }

    @Override
    public Optional<State> display() {
        return run();
    }

    private Optional<State> run() {
        System.out.println("♕ Welcome to Chess ♕");
        System.out.println(SET_TEXT_COLOR_BLUE + help());

        Scanner scanner = new Scanner(System.in);
        printPrompt();
        String line = scanner.nextLine();

        return handleInput(line);
    }

    private Optional<State> handleInput(String input) {
        State result = null;

        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            result = eval(input, params);
        }
        catch (ResponseException ex) {
            alert(ex.getMessage());
        }

        return Optional.ofNullable(result);
    }

    abstract State eval(String input, String... params) throws ResponseException;

    abstract void printPrompt();

    void alert(String msg) {
        System.out.print(SET_TEXT_COLOR_RED + msg);
    }

    void notify(String msg) {
        System.out.print(SET_TEXT_COLOR_BLUE + msg);
    }

    abstract State help();

    State quit() {
        notify("Quiting...");
        return State.QUIT;
    }
}
