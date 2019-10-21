package com.example.iict_admin.classes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.iict_admin.R;

public class DataViewHolder extends RecyclerView.ViewHolder {
    public TextView nameview, tableview;
    public DataViewHolder(@NonNull View itemView) {
        super(itemView);

        nameview = itemView.findViewById(R.id.nameview);
        tableview = itemView.findViewById(R.id.tableno);
    }
}
