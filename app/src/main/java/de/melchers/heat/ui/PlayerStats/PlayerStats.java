package de.melchers.heat.ui.PlayerStats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.melchers.heat.R;
import de.melchers.heat.classes.HeatViewModel;
import de.melchers.heat.classes.Player;
import de.melchers.heat.databinding.FragmentPlayerStatsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerStats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerStats extends Fragment {

    private FragmentPlayerStatsBinding binding;
    private HeatViewModel viewModel;
    private LinearLayout scrollViewChild;
    private TextView numOfFirstPlace;
    private TextView totalPoints;
    private TextView numOfRounds;
    //private  TextView bestTrack;
    private TextView playerColor;


    public PlayerStats() {
        // Empty constructor required
    }

    public static PlayerStats newInstance(String param1, String param2) {
        PlayerStats fragment = new PlayerStats();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null){
//            Bundle bundle = getArguments();
//            this.player = (Player) bundle.get("Player");
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlayerStatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        viewModel = new ViewModelProvider(requireActivity()).get(HeatViewModel.class);
        prepareView();
    }

    private void prepareView(){
        this.scrollViewChild = binding.scrollViewChild;
        View[] playerCards = new View[viewModel.players.size()];
        View tempLayout;
        TextView tempText;
        int count = 0;
        for (Player player :
                viewModel.players) {
            tempLayout = LayoutInflater.from(requireActivity()).inflate(R.layout.template_player_stats, null);
            tempText = tempLayout.findViewWithTag("name");
            tempText.setText(player.getName());

            tempText = tempLayout.findViewWithTag("inpFirstPlace");
            if (player.allPlacements.containsKey(1)){
                tempText.setText(Integer.toString(player.allPlacements.get(1)));
            }

            tempText = tempLayout.findViewWithTag("inpTotalPoints");
            tempText.setText(Integer.toString(player.getTotalScore()));

            tempText = tempLayout.findViewWithTag("inpRoundsPlayed");
            tempText.setText(Integer.toString(player.getTotalRounds()));

            playerCards[count] = tempLayout;
            count++;
        }
        for (int i = 0; i < playerCards.length; i++) {
            // TODO: Spacing is too large
            this.scrollViewChild.addView(playerCards[i]);
        }
//        this.playerName.setText(player.getName());
//        this.numOfFirstPlace.setText(player.allPlacements.get(1));
//        this.totalPoints.setText(player.getTotalScore());
//        this.numOfRounds.setText(player.getTotalRounds());
//        //this.bestTrack.setText(player.bestMap);
//        this.playerColor.setText(player.color);


    }
}