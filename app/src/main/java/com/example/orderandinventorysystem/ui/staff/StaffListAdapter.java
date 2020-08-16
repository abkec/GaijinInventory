package com.example.orderandinventorysystem.ui.staff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.orderandinventorysystem.Model.Staff;
import com.example.orderandinventorysystem.R;

import java.util.ArrayList;
import java.util.List;

public class StaffListAdapter extends RecyclerView.Adapter<StaffListAdapter.ViewHolder> {

    private List<Staff> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private List<Staff> mDataAll;
    // data is passed into the constructor
    public StaffListAdapter(Context context, List<Staff> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mDataAll = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.staff_list_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Staff staff = mData.get(position);
        holder.staffID.setText(staff.getStaffID());
        holder.myTextView.setText(staff.getStaffName());
        holder.mobile.setText(staff.getStaffMobile());
        holder.pay.setText(String.format("%.2f",staff.getStaffPay()));
        holder.department.setText(staff.getDepartment());
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void filterList(ArrayList<Staff> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView,staffID, pay, mobile,department;

        ViewHolder(View itemView) {
            super(itemView);
            staffID = itemView.findViewById(R.id.staff_id_view);
            myTextView = itemView.findViewById(R.id.view_name_staff);
            mobile = itemView.findViewById(R.id.view_mobile_staff);
            pay = itemView.findViewById(R.id.view_pay_staff);
            department = itemView.findViewById(R.id.view_department_staff);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(), staffID.getText().toString(),myTextView.getText().toString());
        }
    }

    // convenience method for getting data at click position
    public Staff getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, String id, String name);
    }

}
