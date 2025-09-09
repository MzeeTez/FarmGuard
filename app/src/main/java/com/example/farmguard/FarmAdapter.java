package com.example.farmguard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FarmAdapter extends RecyclerView.Adapter<FarmAdapter.FarmViewHolder> {

    private List<Farm> farmList;

    public FarmAdapter(List<Farm> farmList) {
        this.farmList = farmList;
    }

    @NonNull
    @Override
    public FarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.farm_list_item, parent, false);
        return new FarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmViewHolder holder, int position) {
        Farm farm = farmList.get(position);
        holder.farmLocation.setText("Location: " + farm.getLocation());
        holder.farmType.setText("Type: " + farm.getFarmType());
        holder.herdSize.setText("Herd Size: " + farm.getHerdSize());
    }

    @Override
    public int getItemCount() {
        return farmList.size();
    }

    static class FarmViewHolder extends RecyclerView.ViewHolder {
        TextView farmLocation;
        TextView farmType;
        TextView herdSize;

        public FarmViewHolder(@NonNull View itemView) {
            super(itemView);
            farmLocation = itemView.findViewById(R.id.farm_location);
            farmType = itemView.findViewById(R.id.farm_type);
            herdSize = itemView.findViewById(R.id.herd_size);
        }
    }
}