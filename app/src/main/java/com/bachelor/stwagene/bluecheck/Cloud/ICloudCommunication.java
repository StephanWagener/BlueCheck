package com.bachelor.stwagene.bluecheck.Cloud;

import com.bachelor.stwagene.bluecheck.Model.Delivery;

/**
 * Created by stwagene on 31.08.2016.
 */
public interface ICloudCommunication
{
    void sendMeasurement(double value);

    void sendDelivery(Delivery delivery);
}
