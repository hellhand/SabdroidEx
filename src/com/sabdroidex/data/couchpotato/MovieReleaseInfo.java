package com.sabdroidex.data.couchpotato;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;

import java.io.Serializable;

/**
 * Created by Marc on 2/06/13.
 */
@JSONElement
public class MovieReleaseInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private String url;
    private Integer age;
    private String description;
    private String content;
    private Integer score;
    private String provider;
    private String providerExtra;
    private String detailUrl;
    private String type;
    private String id;
    private Long size;

    public String getName() {
        return name;
    }

    @JSONSetter(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    @JSONSetter(name = "url")
    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getAge() {
        return age;
    }

    @JSONSetter(name = "url")
    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    @JSONSetter(name = "description")
    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    @JSONSetter(name = "content")
    public void setContent(String content) {
        this.content = content;
    }

    public Integer getScore() {
        return score;
    }

    @JSONSetter(name = "score")
    public void setScore(Integer score) {
        this.score = score;
    }

    public String getProvider() {
        return provider;
    }

    @JSONSetter(name = "provider")
    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderExtra() {
        return providerExtra;
    }

    @JSONSetter(name = "providerExtra")
    public void setProviderExtra(String providerExtra) {
        this.providerExtra = providerExtra;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    @JSONSetter(name = "detail_url")
    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getType() {
        return type;
    }

    @JSONSetter(name = "type")
    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    @JSONSetter(name = "id")
    public void setId(String id) {
        this.id = id;
    }

    public Long getSize() {
        return size;
    }

    @JSONSetter(name = "size")
    public void setSize(Long size) {
        this.size = size;
    }

}
