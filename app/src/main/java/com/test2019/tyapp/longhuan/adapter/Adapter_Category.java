package com.test2019.tyapp.longhuan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.lisener.Clicked_Position_Listener;
import com.test2019.tyapp.longhuan.model.DeviceTypeModel;

import java.util.ArrayList;

public class Adapter_Category extends RecyclerView.Adapter<Adapter_Category.MyViewHolder> {

    private ArrayList<DeviceTypeModel> mDeviceTypeModels;
    private final int NONE_SELECTED = -1;

    private int selected_index = NONE_SELECTED;

    private Clicked_Position_Listener mPosition_listener;

    public Adapter_Category(ArrayList<DeviceTypeModel> models, Clicked_Position_Listener listener){
        this.mDeviceTypeModels = models;
        mPosition_listener = listener;
    }

    public void set_selected_index(int index) {
        this.selected_index = index;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_type, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtName.setText(mDeviceTypeModels.get(position).mName);
        holder.imgIcon.setImageResource(mDeviceTypeModels.get(position).mIcon);

        if (selected_index == position)
            holder.imgCheck.setVisibility(View.VISIBLE);
        else
            holder.imgCheck.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mDeviceTypeModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView txtName;
        public ImageView imgIcon, imgCheck;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.item_name);
//            txtType = (TextView) itemView.findViewById(R.id.item_type);
            imgIcon = (ImageView) itemView.findViewById(R.id.item_image);
            imgCheck = (ImageView) itemView.findViewById(R.id.img_check);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_index = getAdapterPosition();
                    notifyDataSetChanged();
                    mPosition_listener.get_clicked_position(selected_index);
                }
            });
        }
    }
}
