package com.test2019.tyapp.longhuan.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.global.Categories;
import com.test2019.tyapp.longhuan.global.Global;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.test2019.tyapp.longhuan.view.OnItemClickListener;
import com.test2019.tyapp.longhuan.view.OnItemLongClickListener;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.Map;

import static com.test2019.tyapp.longhuan.global.Global.ALPHA_DARK;
import static com.test2019.tyapp.longhuan.global.Global.ALPHA_LIGHT;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private final String TAG = "DeviceAdapter";
    private final int TYPE_DEVICE_MENU = 0x100;

    private Context mContext;
    private ArrayList<DeviceBean> mArrayDevices;
    private LayoutInflater inflater;

    private OnItemClickListener clickListener;
    private OnItemLongClickListener clickLongListener;

    private int selected_index = -1;

    public DeviceAdapter(Context context, ArrayList<DeviceBean> beans, OnItemClickListener listener, OnItemLongClickListener longlistener) {
        mContext = context;
        mArrayDevices = beans;
        clickListener = listener;
        clickLongListener = longlistener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    @NonNull
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_device, parent, false);
        DeviceViewHolder holder = new DeviceViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder deviceViewHolder, final int position) {
        DeviceBean deviceBean = mArrayDevices.get(position);

        PreferenceUtils.set_dev_image(mContext, deviceBean, deviceViewHolder.icon);

        String dev_name = PreferenceUtils.getDevName(mContext, deviceBean.getDevId());
        if (dev_name != null) {
            deviceViewHolder.name.setText(dev_name);
        }
        else {
            deviceViewHolder.name.setText(mArrayDevices.get(position).getName());
        }

        boolean bON = false;

        String deviceName = deviceBean.getName();
        String devNameTmp = deviceName.trim().toLowerCase();


        Map<String, Object> dps = mArrayDevices.get(position).dps;

        if(devNameTmp.contains(Categories.CURTAIN_SWITCH)){
            String switch_status = (String)dps.get(Global.SWITCH_DPID_CMD);
            if(dps.containsKey(Global.CURTAIN_SWITCH)){
                bON = (boolean)dps.get(Global.CURTAIN_SWITCH);
            }
        }else{
            if (dps.containsKey(Global.SWITCH_DPID_CMD)){
                bON = (boolean) dps.get(Global.SWITCH_DPID_CMD);
            }
        }

        if (selected_index == position) {
            if (bON) {          // device is on
                deviceViewHolder.icon.setAlpha(ALPHA_LIGHT);
                deviceViewHolder.lin_wifi_active.setVisibility(View.VISIBLE);
                deviceViewHolder.img_wifi_active.setImageResource(R.drawable.ic_show_active_circle);
            }
            else {              // device is off
                deviceViewHolder.icon.setAlpha(ALPHA_DARK);
                deviceViewHolder.lin_wifi_active.setVisibility(View.GONE);
            }
            deviceViewHolder.name.setAlpha(ALPHA_LIGHT);
            deviceViewHolder.background.setBackgroundColor(mContext.getResources().getColor(R.color.color_item_selected));

        } else {
            if (bON) {
                deviceViewHolder.lin_wifi_active.setVisibility(View.VISIBLE);
                deviceViewHolder.img_wifi_active.setImageResource(R.drawable.ic_show_inactive_circle);
            }
            else {
                deviceViewHolder.lin_wifi_active.setVisibility(View.GONE);
            }
            deviceViewHolder.background.setBackgroundColor(mContext.getResources().getColor(R.color.color_inactive));
            deviceViewHolder.name.setAlpha(ALPHA_DARK);
            deviceViewHolder.icon.setAlpha(ALPHA_DARK);

//            deviceViewHolder.background.setBackground(mContext.getResources().getDrawable(R.drawable.recycle_room_border));
        }
    }

    @Override
    public int getItemCount() {
        return mArrayDevices.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout background;
        public TextView name, tv_wifi_active;
        public ImageView icon, img_wifi_active;
        public LinearLayout lin_wifi_active;

        public DeviceViewHolder (@NonNull View itemView) {
            super(itemView);

            background = itemView.findViewById(R.id.rl_background);
            name = itemView.findViewById(R.id.item_devices_text);
            icon = itemView.findViewById(R.id.item_devices_image);
            lin_wifi_active = itemView.findViewById(R.id.lin_wifi_active);
            img_wifi_active = itemView.findViewById(R.id.img_wifi_active);
            tv_wifi_active = itemView.findViewById(R.id.tv_wifi_active);

            itemView.setOnClickListener((v -> {
                int nPosition = getAdapterPosition();
                DeviceBean cur_deviceBean = mArrayDevices.get(nPosition);

                clickListener.onClick(v, TYPE_DEVICE_MENU, nPosition);

                if (selected_index != nPosition) {
                    selected_index = nPosition;
                    notifyDataSetChanged();
                }
            }));

            itemView.setOnLongClickListener((v -> {
                clickLongListener.onLongClick(v, TYPE_DEVICE_MENU, getAdapterPosition());
                return true;
            }));
        }
    }

}
