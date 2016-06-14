package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.TexasInstrumentsUtils;
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
    private MainActivity activity;

    public LogFragment () {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.log_fragment, container, false);

        activity = (MainActivity) getActivity();

        logText = (TextView) view.findViewById(R.id.log_text);

        initText();

        if (!activity.isShowUUIDInLog())
        {
            convertLogText();
        }

        final ImageView delete = (ImageView) view.findViewById(R.id.delete_log);
        delete.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    delete.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_light_red_button));
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    delete.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_red_button));
                    logText.setText("");
                    ((MainActivity) getActivity()).deleteLogText();
                    return true;
                }
                return false;
            }
        });

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
        if (activity.isShowUUIDInLog())
        {
            this.logText.append(text + "\n");
        }
        else
        {
            this.logText.append(getConvertedText(text) + "\n");
        }
    }

    private void convertLogText()
    {
        this.logText.setText(getConvertedText(this.logText.getText().toString()));
    }

    private String getConvertedText(String text)
    {
        text = text.replaceAll(TexasInstrumentsUtils.UUID_STRING_SERVICE_TEMPERATURE, "\"IR Temperature Service\"");
        text = text.replaceAll(TexasInstrumentsUtils.UUID_STRING_SERVICE_PRESSURE, "\"Pressure Service\"");
        text = text.replaceAll(TexasInstrumentsUtils.UUID_STRING_SERVICE_HUMIDITY, "\"Humidity Service\"");
        text = text.replaceAll(TexasInstrumentsUtils.UUID_STRING_SERVICE_LIGHT_INTENSITY, "\"Light Intensity Service\"");
        return text;
    }
}
