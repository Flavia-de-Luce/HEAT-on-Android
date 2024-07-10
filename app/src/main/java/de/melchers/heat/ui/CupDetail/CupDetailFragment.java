package de.melchers.heat.ui.CupDetail;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.http.HeaderBlock;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.melchers.heat.R;
import de.melchers.heat.classes.HeatViewModel;
import de.melchers.heat.classes.Player;
import de.melchers.heat.classes.Race;
import de.melchers.heat.databinding.FragmentCupBinding;
import de.melchers.heat.databinding.FragmentCupDetailBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CupDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CupDetailFragment extends Fragment {
    private FragmentCupDetailBinding binding;
    private HeatViewModel viewModel;
    private int cupNumber;
    private boolean isCurrentCup;

    public CupDetailFragment() {
        // Required empty public constructor
    }
    public static CupDetailFragment newInstance() {
        CupDetailFragment fragment = new CupDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.cupNumber = bundle.getInt("pos");
            this.isCurrentCup = false;
        } else {
            this.isCurrentCup = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCupDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(HeatViewModel.class);
        if (this.isCurrentCup){
            this.cupNumber = viewModel.cups.size() - 1;
        }
        createTableFromTemplate();
    }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    private void createTableFromTemplate() {
        int cols = viewModel.players.size() + 1;
        // +2 wegen Kopf und Fußzeile
        int rows = viewModel.currentCup.races.size();
        TableLayout tableLayout = requireView().findViewById(R.id.cup_detail_table);
        TextView header = requireView().findViewById(R.id.cup_tHeadRace);
        header.setText("Cup " + (this.cupNumber + 1));
        // Initialise ViewArrays
        View[] temp = new View[cols];
        TextView[] tr_body = new TextView[cols];
        TableRow tr_head;

        // Table Head
        tr_head = new TableRow(requireActivity());
        tr_head.setId(100);
        tr_head.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr_head.setGravity(Gravity.TOP);
        temp[0] = LayoutInflater.from(getActivity()).inflate(R.layout.template_table_text, null);
        tr_body[0] = ((TextView) temp[0]);
        tr_body[0].setText("Rennen Nr.");
        tr_body[0].setTextColor(Color.WHITE);
        tr_body[0].setBackgroundColor(Color.BLACK);
        tr_head.addView(tr_body[0]);
        int h = 1;
        for (Player player:viewModel.players) {
            temp[h] = LayoutInflater.from(getActivity()).inflate(R.layout.template_table_text, null);
            tr_body[h] = ((TextView) temp[h]);
            tr_body[h].setId(h + 101);
            tr_body[h].setText(player.getName());
            tr_body[h].setTextColor(Color.WHITE);
            tr_body[h].setBackgroundColor(Color.BLACK);
            // Adding Content to the TableRow
            tr_head.addView(tr_body[h]);
            h++;
        }

        tableLayout.addView(tr_head);
        TableRow[] tr = new TableRow[viewModel.currentCup.races.size()]; //rows
        TextView[] tr_content = new TextView[(viewModel.players.size() + 1) * viewModel.currentCup.races.size()];
        View[] temp2 = new View[(viewModel.players.size() + 1) * viewModel.currentCup.races.size()];
        int bodyCount = 0;
        int trCount = 0;
        // Table Body
        for (Race race:viewModel.currentCup.races) {
            tr[trCount] = new TableRow(requireActivity());
            tr[trCount].setId(trCount + 200);
            tr[trCount].setGravity(Gravity.TOP);
            temp2[bodyCount] = LayoutInflater.from(getActivity()).inflate(R.layout.template_table_text, null);
            tr_content[bodyCount] = ((TextView) temp2[bodyCount]);
            tr_content[bodyCount].setText((trCount + 1) + ", " + race.getMapName());
            tr_content[bodyCount].setTextColor(Color.WHITE);
            tr_content[bodyCount].setBackgroundColor((trCount % 2 == 0) ? Color.DKGRAY : Color.BLACK);
            tr[trCount].addView(tr_content[bodyCount]);
            bodyCount++;
            for (Player player: viewModel.players) {
                temp2[bodyCount] = LayoutInflater.from(getActivity()).inflate(R.layout.template_table_text, null);
                tr_content[bodyCount] = ((TextView) temp2[bodyCount]);
                tr_content[bodyCount].setId(bodyCount + 300);
                tr_content[bodyCount].setText(Integer.toString(race.getResults().get(player)));
                tr_content[bodyCount].setTextColor(Color.WHITE);
                tr_content[bodyCount].setBackgroundColor((trCount % 2 == 0) ? Color.DKGRAY : Color.BLACK);
                tr[trCount].addView(tr_content[bodyCount]);
                bodyCount++;
            }
            tableLayout.addView(tr[trCount]);
            trCount++;
        }
        View[] tempFooter = new View[viewModel.players.size() + 1];
        TextView[] footerBody = new TextView[viewModel.players.size() + 1];
        TableRow footer = new TableRow(requireActivity());
        tempFooter[0] = LayoutInflater.from(requireActivity()).inflate(R.layout.template_table_text, null);
        footerBody[0] = (TextView)tempFooter[0];
        footerBody[0].setId(12000);
        footerBody[0].setText("Cup Gesamt");
        footerBody[0].setTextColor(Color.WHITE);
        footerBody[0].setBackgroundColor(((trCount) % 2 == 0) ? Color.DKGRAY : Color.BLACK);
        footer.addView(footerBody[0]);
        footer.setGravity(Gravity.TOP);
        int footerCount = 1;
        for (Player player:viewModel.players) {
            tempFooter[footerCount] = LayoutInflater.from(requireActivity()).inflate(R.layout.template_table_text, null);
            footerBody[footerCount] = (TextView)tempFooter[footerCount];
            footerBody[footerCount].setId(12000 + footerCount);
            footerBody[footerCount].setText(Integer.toString(viewModel.currentCup.totalScore.get(player)));
            footerBody[footerCount].setTextColor(Color.WHITE);
            footerBody[footerCount].setBackgroundColor(((trCount) % 2 == 0) ? Color.DKGRAY : Color.BLACK);
            footer.addView(footerBody[footerCount]);
            footerCount++;
        }
        tableLayout.addView(footer);
    }
}