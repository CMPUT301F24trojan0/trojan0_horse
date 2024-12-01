package com.example.trojan0project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.EntrantViewHolder> {

    private List<String> entrantsList;

    public EntrantsAdapter(List<String> entrantsList) {
        this.entrantsList = entrantsList;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrant, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        holder.entrantNameTextView.setText(entrantsList.get(position));
    }

    @Override
    public int getItemCount() {
        return entrantsList.size();
    }

    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrant_name_text_view);
        }
    }
}