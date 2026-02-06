package com.example.lab5_starter;

import android.content.Context;
import android.widget.Toast;

/**
 * Utility class for handling city deletion operations
 */
public class CityDeletionHelper {
    
    private Context context;
    
    public CityDeletionHelper(Context context) {
        this.context = context;
    }
    
    /**
     * Show a confirmation toast when a city is being deleted
     * @param cityName The name of the city being deleted
     */
    public void showDeletionToast(String cityName) {
        String message = "Deleting " + cityName + "...";
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show a success toast when a city is successfully deleted
     * @param cityName The name of the city that was deleted
     */
    public void showDeletionSuccessToast(String cityName) {
        String message = cityName + " has been deleted";
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show an error toast when city deletion fails
     * @param cityName The name of the city that couldn't be deleted
     */
    public void showDeletionErrorToast(String cityName) {
        String message = "Failed to delete " + cityName + ". Please try again.";
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}