package ui;

import client.State;

import java.util.Optional;

public class QuitMenu implements Displayable {
    @Override
    public Optional<State> display() {
        return Optional.of(State.QUIT);
    }
}
