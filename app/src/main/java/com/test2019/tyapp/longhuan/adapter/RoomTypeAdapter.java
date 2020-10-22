package com.test2019.tyapp.longhuan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.R;

import java.util.ArrayList;

public class RoomTypeAdapter extends ArrayAdapter<RoomTypeAdapter.RoomType> {

    private final String TAG = "RoomTypeAdapter";
    private final int NONE_SELECTED = -1;
    private Context mContext;
    private ArrayList<RoomType> array = new ArrayList<>();


    private TextView txtName;
    private ImageView imgIcon;
    private ImageView imgCheck;

    private int selected_index = NONE_SELECTED;
    private String [] roomTypeName = {
            "Room1","Room2","Room3","Room4","Room5","Room6","Room7","Room8","Room9","Room10",
            "Room11","Room12","Room13","Room14","Room15","Room16","Room17","Room18","Room19","Room20",
            "Room21","Room22","Room23","Room24","Room25","Room26","Room27","Room28","Room29","Room30",
            "Room31","Room32","Room33","Room34","Room35","Room36","Room37","Room38","Room39","Room40",
            "Room41","Room42","Room43","Room44","Room45","Room46","Room47","Room48","Room49","Room50",
            "Room51","Room52","Room53","Room54","Room55","Room56"
    };

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

    public RoomTypeAdapter(Context context) {
        super(context, 0);
        mContext = context;
        initRoomTypeList();
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.item_room_type, null);

        txtName = (TextView) v.findViewById(R.id.item_name);
        imgIcon = (ImageView) v.findViewById(R.id.item_image);
        imgCheck = (ImageView) v.findViewById(R.id.img_check);
        txtName.setText(array.get(position).mName);
        imgIcon.setImageResource(array.get(position).mIcon);

        if (selected_index == position) {
            imgCheck.setVisibility(View.VISIBLE);
        } else {
            imgCheck.setVisibility(View.INVISIBLE);
        }

        return v;
    }

    @Override
    public RoomType getItem(int position) {
        return array.get(position);
    }

    public void setSelection(int index) {
        selected_index = index;
        notifyDataSetChanged();
    }

    private void initRoomTypeList() {
        array.clear();
        for (int i = 0; i < 56; i++) {
            RoomType roomType = new RoomType( roomTypeName[i], roomIcons[i]);
            array.add(roomType);
        }
    }

    class RoomType {
        String mName;
        int   mIcon;

        public RoomType(String name, int icon){
            mName = name;
            mIcon = icon;
        }
    }
}
