package de.melchers.heat.ui.notifications;

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

import de.melchers.heat.MainActivity;
import de.melchers.heat.R;
import de.melchers.heat.classes.HeatViewModel;
import de.melchers.heat.classes.Player;
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
        this.createRoundResult();

        view.findViewById(R.id.submit_round_results_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitGameResults(v);
            }
        });
    }

    private void submitGameResults(View view) {
        Player[] players = viewModel.players;
        int viewId = 300;
        EditText temp;
        for (int i = 0; i < players.length; i++){
            temp = requireView().findViewById(viewId + i);
            players[i].setLastPlacement(Integer.parseInt(temp.getText().toString()));
        }
        ((MainActivity)requireActivity()).calculateResults(players);
        Navigation.findNavController(view).navigate(R.id.navigation_dashboard);
    }

    private void createRoundResult(){
        int playerCount = viewModel.players.length;
        Player[] players = viewModel.players;
        LinearLayout layout = requireView().findViewById(R.id.Notification_Layout);
        View[] names = new View[playerCount];
        EditText[] editTexts = new EditText[playerCount];

        for (int i = 0; i < playerCount; i++) {
            names[i] = LayoutInflater.from(requireActivity()).inflate(R.layout.template_add_player_label, null);
            names[i].setId(i + 200);
            ((TextView)names[i]).setText(players[i].getName());
            editTexts[i] = ((EditText)LayoutInflater.from(requireActivity()).inflate(R.layout.template_input_number, null));
            editTexts[i].setId(i + 300);
            editTexts[i].setHint("Platzierung");
            layout.addView(names[i]);
            layout.addView(editTexts[i]);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}