package com.example.koicarehome_prm392.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koicarehome_prm392.R;
import com.example.koicarehome_prm392.data.entities.Fish;

import java.util.ArrayList;
import java.util.List;

public class FishAdapter extends RecyclerView.Adapter<FishAdapter.FishViewHolder> {
    private List<Fish> fishList = new ArrayList<>();
    private OnFishClickListener listener;


    public interface OnFishClickListener {
        void onEditClick(Fish fish);
        void onDeleteClick(Fish fish);
    }

    public FishAdapter(OnFishClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fish, parent, false);
        return new FishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FishViewHolder holder, int position) {
        Fish fish = fishList.get(position);
        holder.tvFishName.setText(fish.fishName);
        holder.tvFishColor.setText("Màu: " + fish.fishColor);
        holder.tvFishSize.setText(fish.length + "cm - " + fish.weight + "g");
        holder.tvPondInfo.setText("Hồ: " + fish.pondId);
        holder.tvFoodAmount.setText("Thức ăn: " + String.format("%.2f", fish.foodAmount) + "g/ngày");

        // Load image
        if (fish.fishImg != null && !fish.fishImg.isEmpty()) {
            holder.ivFish.setImageURI(Uri.parse(fish.fishImg));
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(fish));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(fish));
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }

    public void setFishList(List<Fish> fishList) {
        this.fishList = fishList;
        notifyDataSetChanged();
    }

    static class FishViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFish;
        TextView tvFishName, tvFishColor, tvFishSize, tvFoodAmount, tvPondInfo;
        ImageButton btnEdit, btnDelete;

        public FishViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFish = itemView.findViewById(R.id.ivFish);
            tvFishName = itemView.findViewById(R.id.tvFishName);
            tvFishColor = itemView.findViewById(R.id.tvFishColor);
            tvFishSize = itemView.findViewById(R.id.tvFishSize);
            tvPondInfo = itemView.findViewById(R.id.tvPondInfo); //them thong tin ho ca
            tvFoodAmount = itemView.findViewById(R.id.tvFoodAmount);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
