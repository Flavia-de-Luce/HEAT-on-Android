package de.melchers.heat.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.melchers.heat.R;
import de.melchers.heat.classes.Cup;
import de.melchers.heat.classes.HeatViewModel;
import de.melchers.heat.classes.Player;
import de.melchers.heat.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private HeatViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(HeatViewModel.class);
        if (viewModel.cups.size() == 0) {
            binding.enterMatchBtn.setVisibility(View.INVISIBLE);
        }
        JSONArray playerArray = new JSONArray();
        try {
            for (Player player : viewModel.players) {
                JSONObject temp = new JSONObject();
                temp.put("Name", player.getName());
                temp.put("LatestPlacement", player.getLastPlacement());
                temp.put("TotalScore", player.getTotalScore());
                playerArray.put(temp);
            }
            createTableFromTemplate(playerArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        view.findViewById(R.id.enter_match_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_navigation_notifications);
            }
        });
        view.findViewById(R.id.cancel_match_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_navigation_home);
            }
        });

        view.findViewById(R.id.add_cup_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.cups.size() == 0 && viewModel.currentCup == null) {
                    viewModel.currentCup = new Cup();
                    viewModel.currentCup.id = 1;
                    viewModel.cups.add(viewModel.currentCup);
                    binding.enterMatchBtn.setVisibility(View.VISIBLE);
                    Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_navigation_notifications);
                } else {
                    if (viewModel.cups.contains(viewModel.currentCup)){

                        viewModel.cups.set(viewModel.cups.indexOf(viewModel.currentCup), viewModel.currentCup);
                    } else {
                        viewModel.cups.add(viewModel.currentCup);
                    }
                    viewModel.currentCup = new Cup();
                    viewModel.currentCup.id = viewModel.cups.size() + 1;
//                    viewModel.currentCup
                    Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_navigation_notifications);
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void createTableFromTemplate(JSONArray playerArray) throws JSONException {
        int cols = 3;
        TableLayout tl = requireView().findViewById(R.id.table1);
        // Initialise Data Objects
        JSONArray dataSet = new JSONArray();
        JSONObject data = new JSONObject();
        // Generate Placeholder Data
        data.put("Name", "Peter");
        data.put("LatestPlacement", 1);
        data.put("TotalScore", 12);
        dataSet.put(data);

        // Initialise ViewArrays
        View[] temp = new View[cols];
        TextView[] tr_body = new TextView[cols];
        TableRow[] tr_head = new TableRow[playerArray.length()];
        // For loop to create Table rows
        for (int i = 0; i < playerArray.length(); i++) {
            JSONObject playerList = playerArray.getJSONObject(i);
            String playerName = playerList.getString("Name");
            int playerPlacement = 0;
            int playerScore = 0;
            if (playerList.has("LatestPlacement") && playerList.has("TotalScore")) {
                playerPlacement = playerList.getInt(("LatestPlacement"));
                playerScore = playerList.getInt("TotalScore");
            }


            // Generate Table rows
            tr_head[i] = new TableRow(getActivity());
            tr_head[i].setId(i + 1);
            tr_head[i].setGravity(Gravity.CENTER | Gravity.TOP);
            //tr_head[i].setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT));

            // Generate Textviews
            for (int j = 0; j < cols; j++) {
                // Creating Textviews from Template
                temp[j] = LayoutInflater.from(getActivity()).inflate(R.layout.template_table_text, null);
                tr_body[j] = ((TextView) temp[j]);
                tr_body[j].setId(i + j + 1);

                switch (j) {
                    case 0:
                        tr_body[j].setText(playerName);
                        tr_body[j].setGravity(Gravity.CENTER);
                        break;
                    case 1:
                        tr_body[j].setText(Integer.toString(playerPlacement));
                        tr_body[j].setGravity(Gravity.CENTER);
                        break;
                    case 2:
                        tr_body[j].setText(Integer.toString(playerScore));
                        tr_body[j].setGravity(Gravity.CENTER);
                        break;
                    default:
                        break;
                }
                tr_body[j].setBackgroundColor((i % 2 == 0) ? Color.GRAY : Color.BLACK);
                // Setting TextColor
                tr_body[j].setTextColor(Color.WHITE);
                tr_body[j].setPadding(10, 10, 10, 10);
                tr_head[i].addView(tr_body[j]);
            }
            tl.addView(tr_head[i], new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}