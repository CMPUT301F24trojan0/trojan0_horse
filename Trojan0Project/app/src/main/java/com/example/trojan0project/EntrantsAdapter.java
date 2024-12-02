

package com.example.trojan0project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
/**
 * Purpose:
 * The `EntrantsAdapter` class is a custom RecyclerView adapter for displaying a list of entrants.
 * It binds a list of entrant names to individual views in a RecyclerView, providing a user-friendly
 * and efficient way to display entrant data.
 *
 * Design Rationale:
 * - Extends `RecyclerView.Adapter` to leverage RecyclerView's efficient item recycling.
 * - Utilizes a ViewHolder (`EntrantViewHolder`) to hold references to individual views, improving performance.
 * - Dynamically binds entrant names to the views for flexibility and scalability.
 *
 * Outstanding Issues:
 * - No known issues at this time.
 */

public class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.EntrantViewHolder> {

    private List<String> entrantsList;

    /**
     * Constructs an EntrantsAdapter with the given list of entrants.
     *
     * @param entrantsList A list of entrant names to be displayed in the RecyclerView.
     */
    public EntrantsAdapter(List<String> entrantsList) {
        this.entrantsList = entrantsList;
    }

    /**
     * Called when RecyclerView needs a new {@link EntrantViewHolder} to represent an item.
     * Inflates the layout for an individual entrant item.
     *
     * @param parent The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View (not used here).
     * @return A new instance of {@link EntrantViewHolder}.
     */
    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrant, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder at the specified position in the RecyclerView.
     *
     * @param holder The ViewHolder to update with data.
     * @param position The position of the item in the dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        holder.entrantNameTextView.setText(entrantsList.get(position));
    }

    /**
     * Returns the total number of items in the dataset.
     *
     * @return The size of the entrants list.
     */
    @Override
    public int getItemCount() {
        return entrantsList.size();
    }

    /**
     * The EntrantViewHolder class holds references to the views used for each individual entrant item.
     */
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView;

        /**
         * Constructs an EntrantViewHolder and initializes the view references.
         *
         * @param itemView The view for a single entrant item.
         */
        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrant_name_text_view);
        }
    }
}
