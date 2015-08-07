package com.lihengl.jujutsu.dialogues;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.lihengl.jujutsu.R;
import com.lihengl.jujutsu.models.SearchFilter;
import com.lihengl.jujutsu.utilities.StringUtility;

public class EditFilterDialogue extends DialogFragment implements View.OnClickListener {

    private int selectedIndex;

    public EditFilterDialogue() {}

    public interface FilterApplyListener {
        void onOptionSelected(SearchFilter filter, int selectedIndex);
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
        selectedIndex = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_settings, container);

        final SearchFilter filter = (SearchFilter) getArguments().getSerializable("filter");
        getDialog().setTitle(StringUtility.capitalize(filter.title) + " Filter");

        Spinner sprOptions = (Spinner) view.findViewById(R.id.sprOptions);
        String[] options = filter.options.toArray(new String[filter.options.size()]);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, options);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sprOptions.setAdapter(spinnerArrayAdapter);

        sprOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIndex = position;
            }
        });

        Button btnApply = (Button) view.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        FilterApplyListener listener = (FilterApplyListener) getActivity();
        listener.onOptionSelected((SearchFilter) getArguments().getSerializable("filter"), selectedIndex);
        dismiss();
    }
}
