package com.test2019.tyapp.longhuan.fragment.device;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test2019.tyapp.longhuan.R;

public class SocketFragment extends Fragment {

    private volatile static SocketFragment mSelfFragment;

    public static Fragment newInstance() {
        if (mSelfFragment == null) {
            synchronized (SocketFragment.class) {
                if (mSelfFragment == null) {
                    mSelfFragment = new SocketFragment();
                }
            }
        }
        return mSelfFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_socket, container, false);

        return v;
    }
}
