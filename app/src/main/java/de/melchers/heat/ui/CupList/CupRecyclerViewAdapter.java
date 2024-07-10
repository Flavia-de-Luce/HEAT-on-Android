package de.melchers.heat.ui.CupList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import de.melchers.heat.MainActivity;
import de.melchers.heat.classes.Cup;
import de.melchers.heat.databinding.FragmentCupBinding;

import java.util.List;
public class CupRecyclerViewAdapter extends RecyclerView.Adapter<CupRecyclerViewAdapter.ViewHolder> {
    public List<Cup> mCups;
    public OnClickListener onClickListener;

    public CupRecyclerViewAdapter(List<Cup> cups) {
        this.mCups = cups;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentCupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mCup = mCups.get(position);
        holder.mIdView.setText(String.valueOf(mCups.get(position).id));
        holder.mContentView.setText(mCups.get(position).races.get(mCups.get(position).races.size() - 1).getMapName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null){
                    onClickListener.onClick(holder.getAbsoluteAdapterPosition(), holder.mCup);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCups.size();
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Cup model);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        FragmentCupBinding binding;
        ViewHolder(FragmentCupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            mIdView = binding.cupNumber;
            mContentView = binding.cupDetail;
        }
        final FragmentCupBinding getBinding(){
            return binding;
        }
        final void setBinding(FragmentCupBinding binding){
            this.binding = binding;
        }
        final void setmContentView(CharSequence value){
            binding.cupDetail.setText(value);
        }
        final CharSequence getmContentView() {
            return binding.cupDetail.getText();
        }
        final CharSequence getmIdView() {
            return binding.cupNumber.getText();
        }
        public final TextView mIdView;
        public final TextView mContentView;

        public Cup mCup;

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}