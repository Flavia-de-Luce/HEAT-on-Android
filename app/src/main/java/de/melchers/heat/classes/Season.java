package de.melchers.heat.classes;

import java.util.ArrayList;



public class Season {
    public int id;
    public ArrayList<Cup> cups = new ArrayList<>();
    public ArrayList<Player> players = new ArrayList<>();
    public String lastMapName;
}

class OldSeason {
    public int id;
    public ArrayList<Cup> cups = new ArrayList<>();
}