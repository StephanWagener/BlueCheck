package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

/**
 * Show the Log messages fo the application.
 *
 * Created by stwagene on 02.05.2016.
 */
public class LogFragment extends Fragment
{
    private TextView logText;

    public LogFragment () {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.log, container, false);

        logText = (TextView) view.findViewById(R.id.log_text);

        initText();

        return view;
    }

    private void initText()
    {
        ArrayList<String> texts = getArguments().getStringArrayList("LOG");
        if (texts != null || !texts.isEmpty())
        {
            for (String text : texts)
            {
                logText.append(text + "\n");
            }
        }
        else
        {
            logText.setText("Keine Log-Meldungen verfügbar.");
        }
    }

    public void appendLogText(String text)
    {
        this.logText.append(text + "\n");
    }
}
