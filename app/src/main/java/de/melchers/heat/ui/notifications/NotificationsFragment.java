package de.melchers.heat.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.HashMap;
import de.melchers.heat.MainActivity;
import de.melchers.heat.R;
import de.melchers.heat.classes.HeatViewModel;
import de.melchers.heat.classes.Player;
import de.melchers.heat.classes.Race;
import de.melchers.heat.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private HeatViewModel viewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HeatViewModel.class);
        openMapNameDialog(view);
        this.createRoundResult();

        view.findViewById(R.id.submit_round_results_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserInput(v, getUserInput(v))){
                    submitGameResults(v);
                }
            }
        });

        view.findViewById(R.id.cancel_round_results_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_navigation_notifications_to_navigation_dashboard);
            }
        });
    }

    private ArrayList<String> getUserInput(View view){
        int playerCount = viewModel.players.size();
        int viewId = 300;
        ArrayList<String> userInput = new ArrayList<>();
        EditText temp;
        for (int i = 0; i < playerCount; i++){
            temp = requireView().findViewById(viewId + i);
            userInput.add(temp.getText().toString());
        }
        return userInput;
    }

    private boolean validateUserInput(View view, ArrayList<String> userInput){
        int convInput;
        int playerCount = viewModel.players.size();
        ArrayList<Integer> availablePlacements = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++){
            availablePlacements.add(i);
        }
        // More or less inputs available than expected
        if (userInput.size() != playerCount){
            Toast.makeText(this.getContext(), "Invalide Eingabe", Toast.LENGTH_LONG).show();
            return false;
        } else {
            for (String input :
                    userInput) {
                if (input.isEmpty()){
                    Toast.makeText(this.getContext(), "Eine Platzierung ist leer", Toast.LENGTH_LONG).show();
                    return false;
                }
                convInput = Integer.parseInt(input);
                // Placement higher than the total Player count
                if (convInput > playerCount){
                    Toast.makeText(this.getContext(), "Eine Platzierung ist zu hoch", Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    // Duplicate Placement check
                    if (availablePlacements.contains(convInput)){
                        availablePlacements.remove((Integer) convInput);
                        viewModel.lastUserInput.add(convInput);
                    }else {
                        Toast.makeText(this.getContext(), "Doppelte Platzierung nicht erlaubt", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void submitGameResults(View view) {
        ArrayList<Player> players = viewModel.players;
        int count = 0;
        for (int placement :
                viewModel.lastUserInput) {
            players.get(count).setLastPlacement(placement);
            count++;
        }
        viewModel.lastUserInput.clear();
        ((MainActivity)requireActivity()).calculateResults(players);
        ((MainActivity)requireActivity()).addNewRace(false);

        Navigation.findNavController(view).navigate(R.id.action_navigation_notifications_to_navigation_dashboard);
    }

    private void createRoundResult(){
        int playerCount = viewModel.players.size();

        ArrayList<Player> players = viewModel.players;
        LinearLayout layout = requireView().findViewById(R.id.Notification_Layout);
        View[] names = new View[playerCount];
        EditText[] editTexts = new EditText[playerCount];

        for (int i = 0; i < playerCount; i++) {
            names[i] = LayoutInflater.from(requireActivity()).inflate(R.layout.template_add_player_label, null);
            names[i].setId(i + 200);
            ((TextView)names[i]).setText(players.get(i).getName());
            editTexts[i] = ((EditText)LayoutInflater.from(requireActivity()).inflate(R.layout.template_input_number, null));
            editTexts[i].setId(i + 300);
            editTexts[i].setHint("Platzierung");
            layout.addView(names[i]);
            layout.addView(editTexts[i]);
        }
    }

    private void openMapNameDialog(View view){
        final View dialogView = getLayoutInflater().inflate(R.layout.fragment_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).create();
        dialog.setTitle("Karten Name");
        dialog.setCancelable(false);
        dialog.setView(dialogView);
        final EditText mapName = dialogView.findViewById(R.id.inp_map_name);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!viewModel.currentRace.getMapName().equals("")){
                    viewModel.currentRace = new Race();
                    if (viewModel.currentCup.races.size() == 0) {
                    viewModel.currentCup.races.add(viewModel.currentRace);
                    }
                    viewModel.currentRace.setId(viewModel.currentCup.races.get(viewModel.currentCup.races.size() - 1).getId() + 1);
                }
                viewModel.currentRace.setMapName(mapName.getText().toString());
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Navigation.findNavController(view).navigate(R.id.action_navigation_notifications_to_navigation_dashboard);
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}