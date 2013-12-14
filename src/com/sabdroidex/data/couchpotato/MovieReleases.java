package com.sabdroidex.data.couchpotato;

import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.impl.JSONType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 17/11/13.
 */
public class MovieReleases {

    private List<MovieRelease> releases;

    public List<MovieRelease> getReleases() {
        if (releases == null) {
            releases = new ArrayList<MovieRelease>();
        }
        return releases;
    }

    @JSONSetter(name = "releases", type = JSONType.LIST, objectClazz = MovieRelease.class)
    public void setReleases(List<MovieRelease> releases) {
        this.releases = releases;
    }
}
