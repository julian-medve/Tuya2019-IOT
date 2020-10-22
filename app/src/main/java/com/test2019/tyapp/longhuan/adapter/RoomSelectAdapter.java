package com.test2019.tyapp.longhuan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;
import com.tuya.smart.home.sdk.bean.RoomBean;

import java.util.ArrayList;
import java.util.List;

public class RoomSelectAdapter extends ArrayAdapter {

    private final String TAG = "RoomSelectAdapter";
    private final int NONE_SELECTED = -1;
    private Context mContext;

    LayoutInflater inflater;
    private ArrayList<RoomBean> roomlists;

    private TextView txtName;
    private ImageView imgIcon;
    private ImageView imgCheck;

    private int selected_index = NONE_SELECTED;

    private int [] roomIcons = {

            R.mipmap.ic_room1,R.mipmap.ic_room2,R.mipmap.ic_room3,R.mipmap.ic_room4,R.mipmap.ic_room5,
            R.mipmap.ic_room6,R.mipmap.ic_room7,R.mipmap.ic_room8,R.mipmap.ic_room9,R.mipmap.ic_room10,
            R.mipmap.ic_room11,R.mipmap.ic_room12,R.mipmap.ic_room13,R.mipmap.ic_room14,R.mipmap.ic_room15,
            R.mipmap.ic_room16,R.mipmap.ic_room17,R.mipmap.ic_room18,R.mipmap.ic_room19,R.mipmap.ic_room20,
            R.mipmap.ic_room21,R.mipmap.ic_room22,R.mipmap.ic_room23,R.mipmap.ic_room24,R.mipmap.ic_room25,
            R.mipmap.ic_room26,R.mipmap.ic_room27,R.mipmap.ic_room28,R.mipmap.ic_room29,R.mipmap.ic_room30,
            R.mipmap.ic_room31,R.mipmap.ic_room32,R.mipmap.ic_room33,R.mipmap.ic_room34,R.mipmap.ic_room35,
            R.mipmap.ic_room36,R.mipmap.ic_room37,R.mipmap.ic_room38,R.mipmap.ic_room39,R.mipmap.ic_room40,
            R.mipmap.ic_room41,R.mipmap.ic_room42,R.mipmap.ic_room43,R.mipmap.ic_room44,R.mipmap.ic_room45,
            R.mipmap.ic_room46,R.mipmap.ic_room47,R.mipmap.ic_room48,R.mipmap.ic_room49,R.mipmap.ic_room50,
            R.mipmap.ic_room51,R.mipmap.ic_room52,R.mipmap.ic_room53,R.mipmap.ic_room54,R.mipmap.ic_room55,
            R.mipmap.ic_room56,
    };

    public RoomSelectAdapter(Context context, List<RoomBean> lists) {
        super(context, 0, lists);
        mContext = context;
        roomlists = (ArrayList<RoomBean>) lists;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return roomlists.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v;

        v = inflater.inflate(R.layout.item_room_select, null);

        txtName = (TextView) v.findViewById(R.id.item_name);
        imgIcon = (ImageView) v.findViewById(R.id.item_image);
        imgCheck = (ImageView) v.findViewById(R.id.img_check);

        txtName.setText(roomlists.get(position).getName());
        String channel = Long.toString(roomlists.get(position).getRoomId());

        int image_id = PreferenceUtils.getInt(mContext, channel);
        imgIcon.setImageResource(roomIcons[image_id]);

        if (selected_index == position)
            imgCheck.setVisibility(View.VISIBLE);
        else
            imgCheck.setVisibility(View.INVISIBLE);
        return v;
    }

    @Override
    public RoomBean getItem(int position) {
        return roomlists.get(position);
    }

    public void selectRoom(int position) {
        selected_index = position;
        notifyDataSetChanged();
    }

}
