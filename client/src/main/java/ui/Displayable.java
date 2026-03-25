package ui;

import client.State;

import java.util.Optional;

public interface Displayable {
    Optional<State> display();
}
