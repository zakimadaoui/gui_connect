package com.zmdev.protoplus.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.ViewBuilder;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import java.util.List;

public class WidgetPreviewAdapter extends RecyclerView.Adapter<WidgetPreviewAdapter.WidgetViewHolder> {

    List<ProtoViewAttrs> attrsList;
    private Context mContext;
    private OnWidgetClickedListener listener;

    public WidgetPreviewAdapter(List<ProtoViewAttrs> protoViewAttrs) {
        attrsList = protoViewAttrs;
    }

    @NonNull
    @Override
    public WidgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new WidgetViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_widget, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WidgetViewHolder holder, int position) {
        ProtoViewAttrs attrs = attrsList.get(position);
        if (holder.frame.getChildAt(0) == null) {
            View v = ViewBuilder.generateViewFromAttrs(attrs, mContext);
            ((ProtoView) v).setViewInMode(ProtoView.MODE_NO_TOUCH);
            holder.frame.addView(v); //add the default view to the frame
            holder.title.setText(attrs.getPreviewTitle());
        }
    }

    @Override
    public int getItemCount() {
        return attrsList.size();
    }

    public class WidgetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout frame;
        TextView title;
        public WidgetViewHolder(@NonNull View itemView) {
            super(itemView);
            frame = itemView.findViewById(R.id.widget_item_preview);
            title = itemView.findViewById(R.id.widget_name_txt);
            itemView.findViewById(R.id.catalog_widget_mask).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(attrsList.get(getAdapterPosition()));
        }
    }


    public interface OnWidgetClickedListener {
        void onClick(ProtoViewAttrs attrs);
    }

    public void setOnWidgetClickedListener(OnWidgetClickedListener listener) {
        this.listener = listener;
    }
}

