package com.lihengl.jujutsu.dialogues;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.lihengl.jujutsu.R;
import com.lihengl.jujutsu.models.SearchFilter;
import com.lihengl.jujutsu.utilities.StringUtility;

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

        SearchFilter filter = (SearchFilter) getArguments().getSerializable("filter");
        getDialog().setTitle(StringUtility.capicalize(filter.title) + " Filter");

        Spinner sprOptions = (Spinner) view.findViewById(R.id.sprOptions);
        String[] options = filter.options.toArray(new String[filter.options.size()]);
        options[0] = "all";
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, options);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sprOptions.setAdapter(spinnerArrayAdapter);

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
