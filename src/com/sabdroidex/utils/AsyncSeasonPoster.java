package com.sabdroidex.utils;

import com.sabdroidex.controllers.sickbeard.SickBeardController;


public class AsyncSeasonPoster extends AsyncImage {

    @Override
    protected String getImageURL(Object...params) {
        return SickBeardController.getSeasonPosterURL(SickBeardController.MESSAGE.SHOW_SEASONLIST.toString().toLowerCase(), (Integer) params[2], (Integer) params[4]);
    }

    @Override
    protected String getFilename(Object...params) {
        return "season-" + (Integer) params[4] + ".jpg";
    }
    
}
