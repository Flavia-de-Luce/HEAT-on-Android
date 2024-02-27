package de.melchers.heat.classes;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class Cup {
    public int id;
    public ArrayList<Race> races = new ArrayList<>();
    public HashMap<Player, Integer> totalScore = new HashMap<>();
}

class OldCup {
    public int id;
    public Race currentRace; //race.put("Johanna", 1) [...]
    public ArrayList<Race> races = new ArrayList<>();

//    JSONArray cup; //cup.put(roundJ)
    /**
     * JSONObject temp = new JSONObject();
     * temp.put("Johanna", 1);
     * temp.put("Luis", 2);
     * JSONObject round = new JSONObject();
     * round.put("Great Britain", temp);
     */
    public Race getCurrentRace() {
        return currentRace;
    }

    public void setCurrentRace(Race currentRace) {
        this.currentRace = currentRace;
    }

    public ArrayList<Race> getRaces() {
        return races;
    }

    public void setRaces(ArrayList<Race> races) {
        this.races = races;
    }
}


