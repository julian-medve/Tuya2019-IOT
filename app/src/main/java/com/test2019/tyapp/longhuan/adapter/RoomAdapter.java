package com.test2019.tyapp.longhuan.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.model.RoomModel;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.view.OnItemClickListener;
import com.test2019.tyapp.longhuan.view.OnItemLongClickListener;

import java.util.ArrayList;

import static com.test2019.tyapp.longhuan.global.Global.ALPHA_DARK;
import static com.test2019.tyapp.longhuan.global.Global.ALPHA_LIGHT;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private final String TAG = "RoomAdapter";

    private final int TYPE_ROOM_MENU = 0x010;

    private ArrayList<RoomModel> mArrayRoom;
    private Context mContext;
    private LayoutInflater inflater;

    int selected_index = -1;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

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

    public RoomAdapter(Context context, ArrayList<RoomModel> objects, OnItemClickListener listener, OnItemLongClickListener longListener) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        mArrayRoom = objects;
        clickListener = listener;
        longClickListener = longListener;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_rooms, parent, false);
        RoomViewHolder holder = new RoomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RoomViewHolder roomViewHolder, final int position) {

        roomViewHolder.icon.setImageResource(roomIcons[mArrayRoom.get(position).getRoomType()]);
        roomViewHolder.name.setText(mArrayRoom.get(position).getRoomBean().getName());

        roomViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(v, TYPE_ROOM_MENU, position);
                if (selected_index == position)
                    selected_index = -1;
                else
                    selected_index = position;
                notifyDataSetChanged();
            }
        });

        roomViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.onLongClick(v, TYPE_ROOM_MENU, position);
                return true;
//                return false;
            }
        });

        if (selected_index == position) {
            roomViewHolder.background.setBackgroundColor(mContext.getResources().getColor(R.color.color_item_selected));
            roomViewHolder.icon.setAlpha(ALPHA_LIGHT);
            roomViewHolder.name.setAlpha(ALPHA_LIGHT);
        } else {
//            roomViewHolder.background.setBackground(mContext.getResources().getDrawable(R.drawable.recycle_room_border));
//            roomViewHolder.background.setBackgroundColor(mContext.getResources().getColor(R.color.color_inactive));
            roomViewHolder.background.setBackground(mContext.getResources().getDrawable(R.drawable.menu_background));
            roomViewHolder.icon.setAlpha(ALPHA_DARK);
            roomViewHolder.name.setAlpha(ALPHA_DARK);
        }
    }

    @Override
    public int getItemCount() {
        return mArrayRoom.size();
    }

    class RoomViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout background;
        TextView name;
        ImageView icon;

        public RoomViewHolder(View itemView) {
            super(itemView);
            background = (RelativeLayout) itemView.findViewById(R.id.rl_background);
            name = (TextView) itemView.findViewById(R.id.item_rooms_text);
            icon = (ImageView) itemView.findViewById(R.id.item_rooms_image);
        }
    }

    public void reset() {
        selected_index = -1;
        notifyDataSetChanged();
    }

}
