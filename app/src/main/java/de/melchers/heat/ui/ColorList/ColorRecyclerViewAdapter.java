package de.melchers.heat.ui.ColorList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.melchers.heat.R;
import de.melchers.heat.classes.Cup;
import de.melchers.heat.databinding.FragmentColorListItemBinding;

public class ColorRecyclerViewAdapter extends RecyclerView.Adapter<ColorRecyclerViewAdapter.ViewHolder> {

    private OnClickListener onClickListener;

    public ColorRecyclerViewAdapter(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ViewHolder(FragmentColorListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText("Redd");
        holder.mTextView.setBackgroundResource(R.color.silver);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null){
                    onClickListener.onClick(holder.getAbsoluteAdapterPosition(), holder.mTextView.getText().toString());
                }
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, String color);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        FragmentColorListItemBinding binding;
        public final TextView mTextView;
        ViewHolder(FragmentColorListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            mTextView = binding.text;
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
