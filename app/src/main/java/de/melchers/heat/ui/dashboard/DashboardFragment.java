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
        JSONArray playerArray = new JSONArray();
        try {
            for (Player player : viewModel.players) {
                JSONObject temp = new JSONObject();
                temp.accumulate("Name", player.getName());
                temp.accumulate("LatestPlacement", player.getLastPlacement());
                temp.accumulate("TotalScore", player.getTotalScore());
                playerArray.put(temp);
            }
            createTableFromTemplate(playerArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        view.findViewById(R.id.enter_match_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.navigation_notifications);
            }
        });
        view.findViewById(R.id.cancel_match_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.navigation_home);
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
            // Switching column colors

            tl.addView(tr_head[i], new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));


            // LayoutInflater.from(getActivity()).inflate(R.layout.table_text, null)
        }
    }

    /*

    private void createDynamicTable(View view, int rows, int cells) throws JSONException {
        TableLayout tl = requireView().findViewById(R.id.table1);
        FrameLayout fl = requireView().findViewById(R.id.frameLayout);
        //TableData data = new TableData("Peter", 1, 12);
        //JSONObject Jdata = new JSONObject((Map) data);
        JSONArray dataSet = new JSONArray();
        JSONObject data = new JSONObject();
        JSONObject data2 = new JSONObject();

        data.put("name", "Peter");
        data.put("id", 1);
        data.put("totalScore", 12);
        dataSet.put(data);
        data2.put("name", "Dieter");
        data2.put("id", 2);
        data2.put("totalScore", 10);
        dataSet.put(data2);

        TextView[] textArray = new TextView[3];
        TableRow[] tr_head = new TableRow[rows];


        // Create X Rows (rows)
        for (int i = 0; i < rows; i++) {
            //JSONObject product = data.getJSONObject(i);
            JSONObject playerList = dataSet.getJSONObject(i);
            String playerName = playerList.getString("name");
            int playerId = playerList.getInt(("id"));
            int playerScore = playerList.getInt("totalScore");

            //Create the table rows
            tr_head[i] = new TableRow(getActivity());
            tr_head[i].setId(i + 1);
            tr_head[i].setBackgroundColor(Color.GRAY);
            tr_head[i].setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT));

            // Here create X Cells (cells)
            for (int j = 0; j < cells; j++) {
                textArray[j] = new TextView(getActivity());
                textArray[j].setId(i + 111);
                int height = 150;//TableRow.LayoutParams.MATCH_PARENT;
                int width = 150;//tr_head[i].getLayoutParams().width;
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(width, height);

                textArray[j].setLayoutParams(layoutParams);
                switch (j) {
                    case 0:
                        textArray[j].setText(Integer.toString(playerId));
                        textArray[j].setGravity(Gravity.CENTER);
                        break;
                    case 1:
                        textArray[j].setText(playerName);
                        //textArray[j].setLayoutParams();
                        break;
                    case 2:
                        textArray[j].setText(Integer.toString(playerScore));
                        textArray[j].setGravity(Gravity.TOP);
                        break;
                    default:
                        break;
                }
                textArray[j].setTextColor(Color.WHITE);
                textArray[j].setPadding(10,10,10,10);
                tr_head[i].addView(textArray[j]);
            }
            tl.addView(tr_head[i], new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }
    }

     */

    /*
    private void createTable(View view) {
        TableLayout t1;
        TableLayout tl = requireView().findViewById(R.id.table1);

        TableRow tr_head = new TableRow(getActivity());
        tr_head.setId(10);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView label_text = new TextView(getActivity());
        label_text.setId(20);
        label_text.setText("Hello");
        label_text.setTextColor(Color.WHITE);
        label_text.setPadding(5, 5, 5, 5);
        tr_head.addView(label_text);

        TextView next_text = new TextView(getActivity());
        next_text.setId(21);
        next_text.setText("Goodbye");
        next_text.setTextColor(Color.RED);
        next_text.setPadding(5, 5, 5, 5);
        tr_head.addView(next_text);

        tl.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}