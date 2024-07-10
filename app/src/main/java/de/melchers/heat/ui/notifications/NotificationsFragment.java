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
        openMapNameDialog();
        this.createRoundResult();

        view.findViewById(R.id.submit_round_results_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitGameResults(v);
            }
        });

        view.findViewById(R.id.cancel_round_results_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_navigation_notifications_to_navigation_home);
            }
        });
    }

    private void submitGameResults(View view) {
        ArrayList<Player> players = viewModel.players;
        int viewId = 300;
        EditText temp;
        for (int i = 0; i < players.size(); i++){
            temp = requireView().findViewById(viewId + i);
            players.get(i).setLastPlacement(Integer.parseInt(temp.getText().toString()));
        }
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

    private void openMapNameDialog(){
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
//                viewModel.currentMapName = mapName.getText().toString();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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