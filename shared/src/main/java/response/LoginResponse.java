package response;

import com.google.gson.Gson;

public record LoginResponse(String username, String authToken) {
    public String toJson() {
        return new Gson().toJson(this);
    }
}
