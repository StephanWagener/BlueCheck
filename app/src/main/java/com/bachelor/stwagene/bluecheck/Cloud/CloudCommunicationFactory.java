package com.bachelor.stwagene.bluecheck.Cloud;

/**
 * Created by stwagene on 06.09.2016.
 */
public class CloudCommunicationFactory
{
    private CloudCommunicationFactory() {}

    public static ICloudCommunication getCloudAdapter(CloudAdapterType type)
    {
        if(CloudAdapterType.MOCK.equals(type))
        {
            return new CloudCommunicationMock();
        }
        else if (CloudAdapterType.CUMULOCITY.equals(type))
        {
            return new CumulocityCloudCommunicationAdapter();
        }
        else
        {
            //TODO Exception feuern
            return new CloudCommunicationMock();
        }
    }

    public enum CloudAdapterType
    {
        MOCK,
        CUMULOCITY;
    }
}
