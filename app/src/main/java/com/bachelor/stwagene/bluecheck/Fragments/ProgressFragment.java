package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.R;

/**
 * Show the Log messages fo the application.
 *
 * Created by stwagene on 02.05.2016.
 */
public class ProgressFragment extends Fragment
{
    private TextView progressText;

    public ProgressFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.progress_fragment, container, false);

        progressText = (TextView) view.findViewById(R.id.load_text);

        initText();

        return view;
    }

    private void initText()
    {
        String text = getArguments().getString("PROGRESS");
        if (text == null)
        {
            progressText.setText("Bitte warten...");
        }
        else
        {
            progressText.setText(text);
        }
    }

    public void changeProgressText(String text)
    {
        if (this.progressText != null)
        {
            this.progressText.setText(text);
        }
    }

    public void close()
    {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
        getActivity().getSupportFragmentManager().popBackStack();
        ((MainActivity) getActivity()).setBackButtonGone();
    }
}
