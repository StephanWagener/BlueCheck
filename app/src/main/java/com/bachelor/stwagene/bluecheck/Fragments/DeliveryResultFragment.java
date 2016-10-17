package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.ListManagement.DeliveryResultListAdapter;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.BluetoothTag;
import com.bachelor.stwagene.bluecheck.Model.DeliveryResultListItem;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by student on 08.10.2016.
 */

public class DeliveryResultFragment extends Fragment
{
    private Button details;
    private boolean isDetails = true;

    public DeliveryResultFragment() {}

    @Override
    public void onResume()
    {
        ((MainActivity) getActivity()).setButtonBarVisibility(true);
        ((MainActivity) getActivity()).setBackButtonVisible(false);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.delivery_result_fragment, container, false);

        ListView packagesList = (ListView) view.findViewById(R.id.delivery_result_packages_list);
        ArrayList<DeliveryResultListItem> resultList = getResultList();
        DeliveryResultListAdapter adapter = new DeliveryResultListAdapter((MainActivity) getActivity(), R.layout.delivery_result_packages_list_item, resultList);
        packagesList.setAdapter(adapter);

        details = (Button) view.findViewById(R.id.details_button);
        details.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isDetails)
                {
                    ((MainActivity) getActivity()).openFragment(new DevicesListFragment());
                }
                else
                {
                    ((MainActivity) getActivity()).openFragment(new EndFragment());
                }
            }
        });

        initResultText(resultList, view);

        return view;
    }

    private ArrayList<DeliveryResultListItem> getResultList()
    {
        ArrayList<BluetoothTag> devices = ((MainActivity) getActivity()).getDevices();
        ArrayList<DeliveryResultListItem> items = new ArrayList<>();

        //Reihenfolge beibehalten
        items.add(new DeliveryResultListItem(0, MainActivity.FALSE_LOADED_PRODUCT));
        items.add(new DeliveryResultListItem(0, MainActivity.RIGHT_LOADED_PRODUCT));
        items.add(new DeliveryResultListItem(0, MainActivity.MISSING_PRODUCT));
        items.add(new DeliveryResultListItem(0, MainActivity.UNKNOWN_PRODUCT));

        for (BluetoothTag tag : devices)
        {
            switch (tag.getProduct().getLoadStatus())
            {
                case MainActivity.FALSE_LOADED_PRODUCT:
                    items.get(0).setNumber(items.get(0).getNumber()+1);
                    break;
                case MainActivity.RIGHT_LOADED_PRODUCT:
                    items.get(1).setNumber(items.get(1).getNumber()+1);
                    break;
                case MainActivity.MISSING_PRODUCT:
                    items.get(2).setNumber(items.get(2).getNumber()+1);
                    break;
                case MainActivity.UNKNOWN_PRODUCT:
                    items.get(3).setNumber(items.get(3).getNumber()+1);
                    break;
                default:
                    break;
            }
        }

        Iterator itr = items.iterator();
        DeliveryResultListItem item;
        while (itr.hasNext())
        {
            item = (DeliveryResultListItem) itr.next();
            if (item.getNumber() == 0)
            {
                itr.remove();
            }
        }

        return items;
    }

    public void initResultText(ArrayList<DeliveryResultListItem> resultList, View view)
    {
        boolean isFalse = false;
        boolean isRight = false;
        boolean isMissing = false;
        boolean isUnknown = false;

        for (DeliveryResultListItem item : resultList)
        {
            switch (item.getType())
            {
                case MainActivity.FALSE_LOADED_PRODUCT:
                    isFalse = true;
                    break;
                case MainActivity.RIGHT_LOADED_PRODUCT:
                    isRight = true;
                    break;
                case MainActivity.MISSING_PRODUCT:
                    isMissing = true;
                    break;
                case MainActivity.UNKNOWN_PRODUCT:
                    isUnknown = true;
                    break;
                default:
                    break;
            }
        }

        TextView resultText = (TextView) view.findViewById(R.id.delivery_result_text);

        if (isMissing)
        {
            resultText.setText("Lieferung unvollständig. Bitte die fehlenden Pakete verladen und Scan 1 wiederholen.");
            resultText.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_red_box));
        }
        else
        {
            boolean secondScan = ((MainActivity) getActivity()).isScanOneFinished() && ((MainActivity) getActivity()).isScanTwoFinished();
            if (secondScan)
            {
                if (isRight && !isFalse)
                {
                    resultText.setText("Lieferung vollständig. Sie können den Vorgang jetzt abschließen.");
                    details.setText("Abschließen");
                    isDetails = false;
                    resultText.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_green_box));
                }
                else if (isFalse)
                {
                    resultText.setText("Lieferung unvollständig. Bitte die falschen Pakete ausladen und Scan 1 wiederholen.");
                    resultText.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_red_box));
                }
            }
            else
            {
                resultText.setText("Lieferung vollständig. Entfernen Sie sich vom Beladeort und starten Sie Scan 2.");
                resultText.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_green_box));
            }
        }
    }
}
