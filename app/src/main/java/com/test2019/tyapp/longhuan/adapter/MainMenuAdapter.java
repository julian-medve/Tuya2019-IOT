package com.test2019.tyapp.longhuan.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.model.MainMenuModel;
import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.view.OnItemClickListener;

import java.util.ArrayList;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.SubMenuViewHolder>{

    private final String TAG = "MainMenuAdapter";

    private final int TYPE_HOME_MENU = 0x001;

    private ArrayList<MainMenuModel> mArraySubMenu;
    private Context mContext;
    private LayoutInflater inflater;

    private OnItemClickListener clickListener;

    public MainMenuAdapter(Context context, ArrayList<MainMenuModel> objects, OnItemClickListener listener) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        mArraySubMenu = objects;
        clickListener = listener;
    }

    @Override
    public SubMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_submenu, parent, false);
        SubMenuViewHolder holder = new SubMenuViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(SubMenuViewHolder subMenuViewHolder, final int position) {
        subMenuViewHolder.icon.setImageResource(mArraySubMenu.get(position).getIcon());
        subMenuViewHolder.title.setText(mArraySubMenu.get(position).getTitle());
        subMenuViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(v, TYPE_HOME_MENU, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArraySubMenu.size();
    }

    class SubMenuViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;

        public SubMenuViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.item_sub_menu_text);
            icon = (ImageView) itemView.findViewById(R.id.item_sub_menu_image);
        }
    }
}
