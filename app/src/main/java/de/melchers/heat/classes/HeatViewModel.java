package de.melchers.heat.classes;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class HeatViewModel extends ViewModel {
    public Player[] players;
    public Cup currentCup;
    public Season currentSeason;
//    public Season[] seasons;
    public ArrayList<Season> seasons = new ArrayList<>();
    public String currentMapName;
}
