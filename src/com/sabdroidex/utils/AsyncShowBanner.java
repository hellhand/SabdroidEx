package com.sabdroidex.utils;

import com.sabdroidex.controllers.sickbeard.SickBeardController;


public class AsyncShowBanner extends AsyncImage {
    
    @Override
    protected String getImageURL(Object...params) {
        return SickBeardController.getBannerURL(SickBeardController.MESSAGE.SHOW_GETBANNER.toString().toLowerCase(), (Integer) params[2]);
    }
    
    @Override
    protected String getFilename(Object...params) {
        return "banner.jpg";
    }
    
}
