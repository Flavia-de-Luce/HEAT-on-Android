package de.melchers.heat.classes;

import org.json.JSONObject;

import java.util.HashMap;

public class Race {
    private int id;
    public HashMap<Player, Integer> results;
    private String mapName = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<Player, Integer> getResults() {
        return results;
    }

    public void setResults(HashMap<Player, Integer> results) {
        this.results = results;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}

class OldRace {
    private int id;
    private JSONObject results;
    private String mapName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JSONObject getResults() {
        return results;
    }

    public void setResults(JSONObject results) {
        this.results = results;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}


