package com.sabdroidex.utils;

import com.sabdroidex.controllers.sickbeard.SickBeardController;


public class AsyncShowPoster extends AsyncImage {

    @Override
    protected String getImageURL(Object...params) {
        return SickBeardController.getPosterURL(SickBeardController.MESSAGE.SHOW_GETPOSTER.toString().toLowerCase(), (Integer) params[2]);
    }

    @Override
    protected String getFilename(Object...params) {
        return "poster.jpg";
    }
    
}
