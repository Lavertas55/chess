package response;

import java.util.Collection;

public record ListGamesResponse(Collection<GameResponse> games) {
}
