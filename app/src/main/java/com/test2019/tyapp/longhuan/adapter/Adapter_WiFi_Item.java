package com.test2019.tyapp.longhuan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.lisener.Wifi_Item_Clicked_Listener;
import com.test2019.tyapp.longhuan.model.Item_Wifi;

import java.util.List;

public class Adapter_WiFi_Item extends RecyclerView.Adapter<Adapter_WiFi_Item.MyViewHolder> {

    private Context mContext;
    private List<Item_Wifi> mWifi_items;

    Wifi_Item_Clicked_Listener mItem_clicked_lisener;

    public Adapter_WiFi_Item(List<Item_Wifi> wifi_items, Wifi_Item_Clicked_Listener item_clicked_lisener){
        this.mWifi_items = wifi_items;
        this.mItem_clicked_lisener = item_clicked_lisener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_wifi_item.setText(mWifi_items.get(position).mWifi.SSID);
        if (mWifi_items.get(position).bChecked == true){
            holder.img_wifi_checked.setVisibility(View.VISIBLE);
        }
        else holder.img_wifi_checked.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mWifi_items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_wifi_item;
        public ImageView img_wifi_checked;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_wifi_item = itemView.findViewById(R.id.tv_wifi_item);
            img_wifi_checked = itemView.findViewById(R.id.img_wifi_checked);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nPosition = getAdapterPosition();
                    for (int i = 0; i < mWifi_items.size(); i ++) {
                        mWifi_items.get(i).bChecked = false;
                    }
                    mWifi_items.get(nPosition).bChecked = true;
                    notifyDataSetChanged();
                    mItem_clicked_lisener.get_wifi_SSID(mWifi_items.get(nPosition).mWifi.SSID);
                }
            });
        }
    }
}