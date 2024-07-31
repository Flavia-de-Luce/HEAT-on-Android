package de.melchers.heat.ui.ColorList;

import android.content.Context;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.zip.Inflater;

import de.melchers.heat.R;
import de.melchers.heat.classes.Cup;
import de.melchers.heat.classes.HeatViewModel;
import de.melchers.heat.databinding.FragmentColorListItemBinding;
import de.melchers.heat.databinding.FragmentColorListBinding;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ColorListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class ColorListDialogFragment extends BottomSheetDialogFragment {
    private HeatViewModel viewModel;

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";

    public ColorListDialogFragment(){
    }

    // TODO: Customize parameters
    public static ColorListDialogFragment newInstance(int itemCount) {
        final ColorListDialogFragment fragment = new ColorListDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color_list, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(HeatViewModel.class);
        //binding = FragmentColorListBinding.inflate(inflater, container, false);

        if (view instanceof RecyclerView){
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(viewModel.colorAdapter);
        }

        return view;//binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        final RecyclerView recyclerView = (RecyclerView) view;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(new ColorAdapter(getArguments().getInt(ARG_ITEM_COUNT)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }

//    static class ViewHolder extends RecyclerView.ViewHolder {
//
//        final TextView text;
//
//        ViewHolder(FragmentColorListItemBinding binding) {
//            super(binding.getRoot());
//            text = binding.text;
//        }
//
//    }

//    public static class ColorAdapter extends RecyclerView.Adapter<ViewHolder> {
//
//        private final int mItemCount = 6;
//        public OnClickListener onClickListener;
//
//        public ColorAdapter(int itemCount) {
//            //mItemCount = itemCount;
//        }
//
//        public interface OnClickListener {
//            void onClick(int position, String color);
//        }
//        public void setOnClickListener(ColorListDialogFragment.ColorAdapter.OnClickListener onClickListener){
//            this.onClickListener = onClickListener;
//        }
//        @NonNull
//        @Override
//        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new ViewHolder(FragmentColorListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//            //holder.text.setText(String.valueOf(position));
//            holder.text.setText("Silver");
//            holder.text.setBackgroundResource(R.color.silver);
//
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onClickListener != null){
//                        onClickListener.onClick(holder.getAbsoluteAdapterPosition(), holder.text.getText().toString());
//                    }
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return mItemCount;
//        }
//
//    }
}