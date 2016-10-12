package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.R;

/**
 * Created by stwagene on 11.10.2016.
 */

public class StartFragment extends Fragment
{
    public StartFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        ((MainActivity) getActivity()).setButtonBarVisibility(true);
        return inflater.inflate(R.layout.start_fragment, container, false);
    }
}
