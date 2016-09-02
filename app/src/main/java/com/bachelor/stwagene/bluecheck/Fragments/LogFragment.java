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
import java.util.Calendar;

/**
 * Show the Log messages fo the application.
 *
 * Created by stwagene on 02.05.2016.
 */
public class LogFragment extends Fragment
{
    public static final String LOG_FRAGMENT_ARGUMENT_TEXT = "LOG";
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
        ArrayList<String> texts = getArguments().getStringArrayList(LOG_FRAGMENT_ARGUMENT_TEXT);
        if (texts != null && !texts.isEmpty())
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

    public static String getCurrentTimeString()
    {
        StringBuilder time = new StringBuilder();
        Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour < 10)
        {
            time.append("0");
        }
        time.append(hour);
        time.append(":");

        int minute = c.get(Calendar.MINUTE);
        if (minute < 10)
        {
            time.append("0");
        }
        time.append(minute);
        time.append(":");

        int seconds = c.get(Calendar.SECOND);
        if (seconds < 10)
        {
            time.append("0");
        }
        time.append(seconds);
        time.append(":");

        int milliseconds = c.get(Calendar.MILLISECOND);
        if (milliseconds < 10)
        {
            time.append("0");
        }
        else if (milliseconds < 100)
        {
            time.append("0");
        }
        time.append(milliseconds);

        return time.toString();
    }

}
