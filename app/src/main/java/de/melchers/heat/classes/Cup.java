package de.melchers.heat.classes;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class Cup {
    public int id;
    public ArrayList<Race> races = new ArrayList<>();
    public HashMap<Player, Integer> totalScore = new HashMap<>();
}


