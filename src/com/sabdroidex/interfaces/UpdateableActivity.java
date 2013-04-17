package com.sabdroidex.interfaces;

import com.sabdroidex.data.sabnzbd.SabnzbdStatus;


public interface UpdateableActivity {
    
    /**
     * This method will allow to refresh the labels in the targetted activity
     * 
     * @param status an instance of a {@link SabnzbdStatus} object
     */
    public void updateLabels(SabnzbdStatus status);

    /**
     * This method defines if the refresh button should be show as active or not
     * 
     * @param showAsUpdate
     */
    public void updateState(boolean showAsUpdate);
}
