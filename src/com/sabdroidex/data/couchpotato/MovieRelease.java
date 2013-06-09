package com.sabdroidex.data.couchpotato;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JSONElement
public class MovieRelease implements Serializable, Comparable<MovieRelease> {

    /**
     *
     */
    private static final long serialVersionUID = 5994990425094361982L;
    private List<MovieFile> movieFiles;
    private MovieReleaseInfo movieReleaseInfo;
    private Integer qualityId;
    private Integer statusId;
    private Long lastEdit;
    private String identifier;
    private Integer id;

    public List<MovieFile> getMovieFiles() {
        if (movieFiles == null) {
            movieFiles = new ArrayList<MovieFile>();
        }
        return movieFiles;
    }

    @JSONSetter(name = "files", type = JSONType.LIST, objectClazz = MovieFile.class)
    public void setMovieFiles(List<MovieFile> releases) {
        this.movieFiles = releases;
    }

    public MovieReleaseInfo getMovieReleaseInfo() {
        return movieReleaseInfo;
    }

    @JSONSetter(name = "info", type = JSONType.JSON_OBJECT)
    public void setMovieReleaseInfo(MovieReleaseInfo movieReleaseInfo) {
        this.movieReleaseInfo = movieReleaseInfo;
    }

    public Integer getQualityId() {
        return qualityId;
    }

    @JSONSetter(name = "quality_id")
    public void setQualityId(Integer qualityId) {
        this.qualityId = qualityId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    @JSONSetter(name = "status_id")
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Long getLastEdit() {
        return lastEdit;
    }

    @JSONSetter(name = "last_edit")
    public void setLastEdit(Long lastEdit) {
        this.lastEdit = lastEdit;
    }

    public String getIdentifier() {
        return identifier;
    }

    @JSONSetter(name = "identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getId() {
        return id;
    }

    @JSONSetter(name = "id")
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int compareTo(MovieRelease another) {
        return getMovieReleaseInfo().getScore().compareTo(((MovieRelease) another).getMovieReleaseInfo().getScore());
    }
}
