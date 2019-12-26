package com.mdhgroup2.postmor.Register;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mdhgroup2.postmor.R;

public class register3 extends Fragment {

    private Register3ViewModel mViewModel;
    private static final String KEY_POSITION = "position";

    public static register3 newInstance(int position) {
        register3 reg = new register3();
        Bundle args = new Bundle();

        args.putInt(KEY_POSITION, position);
        reg.setArguments(args);

        return(reg);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register3_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(Register3ViewModel.class);
        // TODO: Use the ViewModel
    }

}