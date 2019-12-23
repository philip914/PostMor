package com.mdhgroup2.postmor.Box;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mdhgroup2.postmor.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class BoxInboxFragment extends Fragment {
    private static final String VIEW_NUMBER = "section_number";

    private BoxViewModel boxViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private int index;


    public static BoxInboxFragment newInstance(int index) {
        BoxInboxFragment fragment = new BoxInboxFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(VIEW_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boxViewModel = ViewModelProviders.of(this).get(BoxViewModel.class);
        this.index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(VIEW_NUMBER);
        }
        boxViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_inbox_fragment, container, false);
//        final TextView textView = view.findViewById(R.id.textViewInbox);
//        boxViewModel.setIndex(index);

        recyclerView = view.findViewById(R.id.boxRecyclerInboxView);
        layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new BoxRecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);

//        boxViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return view;
    }
}