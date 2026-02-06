package com.example.lab5_starter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A callback class that handles swipe-to-delete functionality for cities in RecyclerView.
 * When a user swipes right on a city item, it will trigger the deletion process.
 */
public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private CityRecyclerAdapter adapter;
    private Drawable deleteIcon;
    private int intrinsicWidth;
    private int intrinsicHeight;
    private ColorDrawable background;
    private int backgroundColor;

    /**
     * Constructor for SwipeToDeleteCallback
     * @param adapter The RecyclerView adapter that handles the city list
     * @param context The context to get drawable resources
     */
    public SwipeToDeleteCallback(CityRecyclerAdapter adapter, Context context) {
        super(0, ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        
        // Set up the delete icon and background
        deleteIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete);
        intrinsicWidth = deleteIcon.getIntrinsicWidth();
        intrinsicHeight = deleteIcon.getIntrinsicHeight();
        backgroundColor = Color.parseColor("#f44336");
        background = new ColorDrawable();
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, 
                         @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        adapter.deleteItem(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, 
                           @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, 
                           int actionState, boolean isCurrentlyActive) {
        
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getBottom() - itemView.getTop();
        boolean isCanceled = dX == 0f && !isCurrentlyActive;

        if (isCanceled) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), 
                       (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        // Draw the red delete background
        background.setColor(backgroundColor);
        background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), 
                           itemView.getBottom());
        background.draw(c);

        // Calculate position of delete icon
        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getLeft() + deleteIconMargin;
        int deleteIconRight = itemView.getLeft() + deleteIconMargin + intrinsicWidth;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;

        // Draw the delete icon
        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteIcon.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * Clear the canvas for the given coordinates
     */
    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, new android.graphics.Paint());
    }
}