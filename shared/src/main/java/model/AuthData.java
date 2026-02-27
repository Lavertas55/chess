package model;

import com.google.gson.Gson;

public record AuthData(String username, String authToken) {

    public String toJson() {
        return new Gson().toJson(this);
    }
}