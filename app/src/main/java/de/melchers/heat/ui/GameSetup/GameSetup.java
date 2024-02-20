package de.melchers.heat.ui.GameSetup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.melchers.heat.MainActivity;
import de.melchers.heat.R;
import de.melchers.heat.classes.Cup;
import de.melchers.heat.classes.HeatViewModel;
import de.melchers.heat.classes.Player;
import de.melchers.heat.classes.Race;
import de.melchers.heat.classes.Season;

public class GameSetup extends Fragment {
    private int playerCount = 1;
    private HeatViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HeatViewModel.class);

        view.findViewById(R.id.addPlayer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTableRow(view);
            }
        });

        view.findViewById(R.id.submit_add_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPlayers(view);
            }
        });
    }

    private void submitPlayers(View view) {
//        String[] playerNames = new String[this.playerCount];
        Player[] players = new Player[this.playerCount];
        int viewId = 10000;

        EditText temp = requireView().findViewById(R.id.inpName);
//        playerNames[0] = temp.getText().toString();
        players[0] = new Player(temp.getText().toString());
        for (int i = 1; i < this.playerCount; i++) {
            EditText temp2 = requireView().findViewById(viewId + i);
//            playerNames[i] = temp2.getText().toString();
            players[i] = new Player(temp2.getText().toString());
        }
//        this.viewModel.playerNames = playerNames;
        this.viewModel.players = players;
//        Dialog().show(supportFragmentManager, "Dialog title?");
//        Dialog dialog = new Dialog();
//        dialog.show(requireActivity().getSupportFragmentManager(), "map_dialog");
//        openDialog();
        ((MainActivity)requireActivity()).addNewRace(true);
        Navigation.findNavController(requireView()).navigate(R.id.action_navigation_game_setup_to_navigation_dashboard);
    }

    private void openDialog() {
        final View dialogView = getLayoutInflater().inflate(R.layout.fragment_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).create();
        dialog.setTitle("Karten Name");
        dialog.setCancelable(false);
        dialog.setView(dialogView);
        final EditText mapName = dialogView.findViewById(R.id.inp_map_name);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.currentMapName = mapName.getText().toString();
//                ((MainActivity)requireActivity()).addNewRace(true);
//                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_game_setup_to_navigation_dashboard);
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

    private void createTableRow(View view) {
        if (this.playerCount < 6) {

            TableLayout tl = requireView().findViewById(R.id.addplayerTable);
            View[] temp = new View[2];
            TextView[] tr_body = new TextView[2];
            TableRow tr_head = new TableRow(getActivity());

            tr_head.setId(this.playerCount);
            tr_head.setGravity(Gravity.START);

            temp[0] = LayoutInflater.from(getActivity()).inflate(R.layout.template_add_player_label, null);
            tr_body[0] = ((TextView) temp[0]);
            tr_body[0].setId(this.playerCount + 5000);
            temp[1] = LayoutInflater.from(getActivity()).inflate(R.layout.template_add_player_input, null);
            tr_body[1] = ((EditText) temp[1]);
            tr_body[1].setId(this.playerCount + 10000);

            tr_head.addView(tr_body[0]);
            tr_head.addView(tr_body[1]);
            tl.addView(tr_head, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            this.playerCount++;
        } else {
            Toast.makeText(requireContext(), "Mehr als 6 Spieler werden nicht unterstützt :/", Toast.LENGTH_SHORT).show();
        }
    }
}