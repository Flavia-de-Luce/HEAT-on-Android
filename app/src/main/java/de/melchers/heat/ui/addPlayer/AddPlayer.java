package de.melchers.heat.ui.addPlayer;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Array;
import java.sql.Time;

import de.melchers.heat.MainActivity;
import de.melchers.heat.R;
import jxl.write.DateTime;

public class AddPlayer extends Fragment {
    private int playerCount = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

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
        String[] playerNames = new String[this.playerCount];
        Bundle bundle = new Bundle();
        int viewId = 10000;

        EditText temp = (EditText) getView().findViewById(R.id.inpName);
        playerNames[0] = temp.getText().toString();
        for (int i = 1; i < this.playerCount; i++) {
            EditText temp2 = (EditText) getView().findViewById(viewId + i);
            playerNames[i] = temp2.getText().toString();


        }
        bundle.putStringArray("PlayerNames", playerNames);

        Navigation.findNavController(view).navigate(R.id.navigation_dashboard, bundle);
    }

    private void createTableRow(View view) {
        if (this.playerCount < 6){

            TableLayout tl = requireView().findViewById(R.id.addplayerTable);
            View[] temp = new View[2];
            TextView[] tr_body = new TextView[2];
            TableRow tr_head = new TableRow(getActivity());

            tr_head.setId(this.playerCount);
            tr_head.setGravity(Gravity.START);

            temp[0] = LayoutInflater.from(getActivity()).inflate(R.layout.add_player_label, null);
            tr_body[0] = ((TextView) temp[0]);
            tr_body[0].setId(this.playerCount + 5000);
            temp[1] = LayoutInflater.from(getActivity()).inflate(R.layout.add_player_input, null);
            tr_body[1] = ((EditText) temp[1]);
            tr_body[1].setId(this.playerCount + 10000);

            tr_head.addView(tr_body[0]);
            tr_head.addView(tr_body[1]);
            tl.addView(tr_head, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            this.playerCount++;
        }
    }
}