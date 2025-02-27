package com.zmdev.protoplus.Adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.zmdev.protoplus.App;
import com.zmdev.protoplus.R;
import org.jetbrains.annotations.NotNull;

public class SwipeToEdit extends ItemTouchHelper.SimpleCallback {
    private final OnItemSwipedListener onSwiped;
    private final Drawable background;
    private final Drawable icon;

    public SwipeToEdit(OnItemSwipedListener onSwiped, Context context) {
        super(0, ItemTouchHelper.LEFT);
        this.onSwiped = onSwiped;
        background = ContextCompat.getDrawable(context, R.drawable.edit_card);
        icon = ContextCompat.getDrawable(context, R.drawable.ic_edit);
    }

    @Override
    public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
        return false;
    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
       onSwiped.onItemSwiped(viewHolder.getAdapterPosition());
    }


    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        //get item view to determine bounds
        View itemView = viewHolder.itemView;
        itemView.setAlpha(1+ dX/ App.screen_width);

        //creating a custom background for swipe delete
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();
        int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
        int iconRight = itemView.getRight() - iconMargin;
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

        if (dX > -200) background.setAlpha((int) ((dX / -200) * 255));
        background.setBounds(viewHolder.itemView.getRight() + (int) dX + 20 , viewHolder.itemView.getTop()+10, viewHolder.itemView.getRight(), viewHolder.itemView.getBottom()-10);
        background.draw(c);
        icon.draw(c);

    }

}
