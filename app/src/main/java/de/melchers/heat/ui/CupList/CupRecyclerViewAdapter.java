package de.melchers.heat.ui.CupList;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import de.melchers.heat.classes.Cup;
import de.melchers.heat.ui.placeholder.PlaceholderContent.PlaceholderItem;
import de.melchers.heat.databinding.FragmentCupBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CupRecyclerViewAdapter extends RecyclerView.Adapter<CupRecyclerViewAdapter.ViewHolder> {

//    private final List<PlaceholderItem> mValues;
    private List<Cup> mCups;

    public CupRecyclerViewAdapter(List<Cup> cups) {
        mCups = cups;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentCupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mCup = mCups.get(position);
        holder.mIdView.setText(String.valueOf(mCups.get(position).id));
        holder.mContentView.setText(mCups.get(position).races.get(mCups.get(position).races.size() - 1).getMapName());
    }

    @Override
    public int getItemCount() {
        return mCups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
//        public PlaceholderItem mItem;
        public Cup mCup;

        public ViewHolder(FragmentCupBinding binding) {
            super(binding.getRoot());
            mIdView = binding.cupNumber;
            mContentView = binding.cupDetail;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}