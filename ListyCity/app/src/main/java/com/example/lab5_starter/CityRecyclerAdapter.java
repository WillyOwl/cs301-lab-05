package com.example.lab5_starter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

/**
 * RecyclerView adapter for displaying cities with swipe-to-delete functionality
 */
public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.CityViewHolder> {

    private ArrayList<City> cities;
    private Context context;
    private CollectionReference citiesRef;
    private OnCityClickListener onCityClickListener;
    private CityDeletionHelper deletionHelper;

    /**
     * Interface for handling city item clicks
     */
    public interface OnCityClickListener {
        void onCityClick(City city);
    }

    /**
     * Constructor for CityRecyclerAdapter
     * @param context The context
     * @param cities List of cities
     * @param citiesRef Firestore collection reference for cities
     * @param listener Click listener for city items
     */
    public CityRecyclerAdapter(Context context, ArrayList<City> cities, 
                              CollectionReference citiesRef, OnCityClickListener listener) {
        this.context = context;
        this.cities = cities;
        this.citiesRef = citiesRef;
        this.onCityClickListener = listener;
        this.deletionHelper = new CityDeletionHelper(context);
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        City city = cities.get(position);
        holder.cityName.setText(city.getName());
        holder.cityProvince.setText(city.getProvince());

        holder.itemView.setOnClickListener(v -> {
            if (onCityClickListener != null) {
                onCityClickListener.onCityClick(city);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    /**
     * Delete a city item at the specified position
     * @param position The position of the item to delete
     */
    public void deleteItem(int position) {
        if (position >= 0 && position < cities.size()) {
            City cityToDelete = cities.get(position);
            
            // Show deletion in progress
            deletionHelper.showDeletionToast(cityToDelete.getName());
            
            // Delete from Firestore
            citiesRef.document(cityToDelete.getName())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "City successfully deleted!");
                        deletionHelper.showDeletionSuccessToast(cityToDelete.getName());
                        // Remove from local list and notify adapter
                        cities.remove(position);
                        notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error deleting city", e);
                        deletionHelper.showDeletionErrorToast(cityToDelete.getName());
                        // If deletion fails, notify that the item changed to restore the view
                        notifyItemChanged(position);
                    });
        }
    }

    /**
     * ViewHolder class for city items
     */
    public class CityViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;
        TextView cityProvince;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.textCityName);
            cityProvince = itemView.findViewById(R.id.textCityProvince);
        }
    }
}