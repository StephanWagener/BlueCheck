package com.bachelor.stwagene.bluecheck.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.R;

/**
 * Created by stwagene on 12.10.2016.
 */

public class DeliverySelectionFragment extends Fragment
{
    public DeliverySelectionFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.delivery_selection_fragment, container, false);

        final AutoCompleteTextView delivery = (AutoCompleteTextView) view.findViewById(R.id.delivery_text);
        Button save = (Button) view.findViewById(R.id.delivery_save_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, ((MainActivity) getActivity()).getCurrentDeliveries());
        delivery.setThreshold(1);
        delivery.setAdapter(adapter);
        delivery.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                View contextView = getActivity().getCurrentFocus();
                if (contextView != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(contextView.getWindowToken(), 0);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((MainActivity) getActivity()).getCurrentDeliveries().contains(delivery.getText().toString()))
                {
                    ((MainActivity) getActivity()).getSharedPreferences()
                            .edit()
                            .putString(SettingsFragment.CURRENT_DELIVERY, delivery.getText().toString())
                            .apply();

                    Toast.makeText(getActivity().getApplicationContext(), "Erfolgreich gespeichert.", Toast.LENGTH_LONG).show();

                    ((MainActivity) getActivity()).openFragment(new StartFragment());
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Keine gültige Lieferung.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }
}
