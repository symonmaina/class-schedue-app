package com.example.myapplication.classscheduleapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private Context context;
    private List<ClassModel> classList;

    public ClassAdapter(Context context, List<ClassModel> classList) {
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassModel classModel = classList.get(position);

        holder.unitCode.setText(classModel.getUnitCode());
        holder.className.setText(classModel.getClassName());
        holder.day.setText(classModel.getDay());
        holder.time.setText(classModel.getStartTime() + " - " + classModel.getEndTime());
        holder.location.setText(classModel.getLocation());

        // Open ClassDetailsActivity when an item is clicked
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClassDetailsActivity.class);
            intent.putExtra("unitCode", classModel.getUnitCode());  // Primary key
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView unitCode, className, day, time, location;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            unitCode = itemView.findViewById(R.id.itemUnitCode);
            className = itemView.findViewById(R.id.itemClassName);
            day = itemView.findViewById(R.id.itemDay);
            time = itemView.findViewById(R.id.itemTime);
            location = itemView.findViewById(R.id.itemLocation);
        }
    }

    // Method to update data dynamically
    public void updateClassList(List<ClassModel> newList) {
        classList.clear();
        classList.addAll(newList);
        notifyDataSetChanged();
    }
}
