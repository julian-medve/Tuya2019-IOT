package com.test2019.tyapp.longhuan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.model.DeviceTypeModel;

import java.util.ArrayList;

public class DeviceTypeAdapter extends ArrayAdapter<DeviceTypeModel> {

    private final String TAG = "DeviceTypeAdapter";

    private Context mContext;
    private ArrayList<DeviceTypeModel> array;
    private final int NONE_SELECTED = -1;

    private TextView txtName;
    private TextView txtType;
    private ImageView imgIcon;
    private ImageView imgCheck;

    private int selected_index = NONE_SELECTED;

    public DeviceTypeAdapter(Context context, ArrayList<DeviceTypeModel> types) {
        super(context, 0);
        mContext = context;
        array = types;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.item_device_type, null);

        txtName = (TextView) v.findViewById(R.id.item_name);
        txtType = (TextView) v.findViewById(R.id.item_type);
        imgIcon = (ImageView) v.findViewById(R.id.item_image);
        imgCheck = (ImageView) v.findViewById(R.id.img_check);

        txtName.setText(array.get(position).mName);

        switch (array.get(position).mDeviceType) {
            case DeviceTypeModel.DEVICE_WIFI:
                break;
            case DeviceTypeModel.DEVICE_ZIGBEE:
                txtType.setText(R.string.text_device_zigbee);
                break;
            case DeviceTypeModel.DEVICE_BLUETOOTH:
                txtType.setText(R.string.text_device_bluetooth);
                break;
        }

        imgIcon.setImageResource(array.get(position).mIcon);

        if (selected_index == position)
            imgCheck.setVisibility(View.VISIBLE);
        else
            imgCheck.setVisibility(View.INVISIBLE);

        return v;
    }

    @Override
    public DeviceTypeModel getItem(int position) {
        return array.get(position);
    }

    public void selectDeviceType(int position) {
        selected_index = position;
        notifyDataSetChanged();
    }
}
