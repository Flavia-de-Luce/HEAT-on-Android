package de.melchers.heat.classes;

import android.view.View;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import de.melchers.heat.R;
import de.melchers.heat.ui.CupList.CupRecyclerViewAdapter;


public class HeatViewModel extends ViewModel {
//    public ArrayList<Season> seasons = new ArrayList<>();
    public ArrayList<Cup> cups = new ArrayList<>();
    public ArrayList<Player> players = new ArrayList<>();
    public Cup currentCup; // = new Cup();
    public Race currentRace = new Race();
    public CupRecyclerViewAdapter cupAdapter;
    public ArrayList<Integer> lastUserInput = new ArrayList<>();
}
