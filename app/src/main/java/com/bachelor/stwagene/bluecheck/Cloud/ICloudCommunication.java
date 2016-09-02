package com.bachelor.stwagene.bluecheck.Cloud;

import java.util.ArrayList;

/**
 * Created by stwagene on 31.08.2016.
 */
public interface ICloudCommunication
{
    void sendMeasurement(double value);

    void sendBlePackageList(ArrayList<String> addresses, String deliveryID);
}
