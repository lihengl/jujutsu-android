package com.lihengl.jujutsu.dialogues;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lihengl.jujutsu.R;
import com.lihengl.jujutsu.models.SearchFilter;

public class EditFilterDialogue extends DialogFragment implements View.OnClickListener {

    public EditFilterDialogue() {}

    public interface FilterApplyListener {
        void onApplyFilter(SearchFilter filter);
    }

    public static EditFilterDialogue newInstance(SearchFilter filter) {
        Bundle arguments = new Bundle();
        arguments.putSerializable("filter", filter);

        EditFilterDialogue fragment = new EditFilterDialogue();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_settings, container);
        getDialog().setTitle("Color Filter");

        Button btnApply = (Button) view.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        FilterApplyListener listener = (FilterApplyListener) getActivity();
        listener.onApplyFilter((SearchFilter) getArguments().getSerializable("filter"));
        dismiss();
    }
}
