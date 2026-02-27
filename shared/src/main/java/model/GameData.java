package model;

import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, String gameString) {

    public String toJson() {
        return new Gson().toJson(this);
    }
}