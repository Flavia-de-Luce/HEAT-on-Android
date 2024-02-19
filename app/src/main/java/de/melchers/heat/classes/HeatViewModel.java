package de.melchers.heat.classes;

import androidx.lifecycle.ViewModel;

import org.json.JSONArray;

import java.lang.reflect.Array;

public class HeatViewModel extends ViewModel {
    public JSONArray playerArray;
    public Player[] players;
//    public String[] playerNames;
    public int[] playerPlacement;
    public int[] playerScore;
}
