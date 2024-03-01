package de.melchers.heat.ui.CupList;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import de.melchers.heat.R;
import de.melchers.heat.classes.HeatViewModel;
import de.melchers.heat.ui.placeholder.PlaceholderContent;

/**
 * A fragment representing a list of Items.
 */
public class CupFragment extends Fragment implements AdapterView.OnClickListener {
    private HeatViewModel viewModel;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CupFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CupFragment newInstance(int columnCount) {
        CupFragment fragment = new CupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cup_list, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(HeatViewModel.class);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new CupRecyclerViewAdapter(viewModel.cups));
        }
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        RecyclerView listView = (RecyclerView) requireActivity().findViewById(R.id.cup_recycler);
        listView.setOnClickListener(this);
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//    }

    @Override
    public void onClick(View v) {
        Toast.makeText(requireActivity(), "TEST", Toast.LENGTH_LONG).show();
    }
}