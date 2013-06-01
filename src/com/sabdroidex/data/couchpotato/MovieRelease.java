package com.sabdroidex.data.couchpotato;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sabdroidex.fragments.dialogs.couchpotato.MovieFile;
import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;

@JSONElement
public class MovieRelease implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5994990425094361982L;
    private List<MovieFile> movieFiles;
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

    @JSONSetter(name = "files", objectClazz = MovieFile.class)
    public void setMovieFiles(List<MovieFile> releases) {
        this.movieFiles = releases;
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
}
