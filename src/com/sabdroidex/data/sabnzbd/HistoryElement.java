package com.sabdroidex.data.sabnzbd;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.impl.JSONType;

import java.io.Serializable;
import java.util.List;

@JSONElement
public class HistoryElement implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5466001889105756075L;
    
    private String actionLine;
    private String showDetails;
    private String scriptLog;
    private Object meta;
    private String failMessage;
    private Boolean loaded;
    private Integer id;
    private String size;
    private String category;
    private String pp;
    private Integer completeness;
    private String script;
    private String nzbName;
    private Integer downloadTime;
    private String storage;
    private String status;
    private String scriptLine;
    private Long completed;
    private String nzoId;
    private Long downloaded;
    private String report;
    private String path;
    private Integer postprocTime;
    private String name;
    private String url;
    private Long bytes;
    private String urlInfo;
    private List<DownloadLog> stageLogs;
    
    public String getActionLine() {
        return actionLine;
    }
    
    public void setActionLine(String actionLine) {
        this.actionLine = actionLine;
    }
    
    public String getShowDetails() {
        return showDetails;
    }
    
    public void setShowDetails(String showDetails) {
        this.showDetails = showDetails;
    }
    
    public String getScriptLog() {
        return scriptLog;
    }
    
    public void setScriptLog(String scriptLog) {
        this.scriptLog = scriptLog;
    }
    
    public Object getMeta() {
        return meta;
    }
    
    public void setMeta(Object meta) {
        this.meta = meta;
    }
    
    public String getFailMessage() {
        return failMessage;
    }
    
    @JSONSetter(name = "fail_message")
    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }
    
    public Boolean getLoaded() {
        return loaded;
    }
    
    public void setLoaded(Boolean loaded) {
        this.loaded = loaded;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getSize() {
        return size;
    }
    
    @JSONSetter(name = "size")
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPp() {
        return pp;
    }
    
    public void setPp(String pp) {
        this.pp = pp;
    }
    
    public Integer getCompleteness() {
        return completeness;
    }
    
    public void setCompleteness(Integer completeness) {
        this.completeness = completeness;
    }
    
    public String getScript() {
        return script;
    }
    
    public void setScript(String script) {
        this.script = script;
    }
    
    public String getNzbName() {
        return nzbName;
    }
    
    public void setNzbName(String nzbName) {
        this.nzbName = nzbName;
    }
    
    public Integer getDownloadTime() {
        return downloadTime;
    }
    
    public void setDownloadTime(Integer downloadTime) {
        this.downloadTime = downloadTime;
    }
    
    public String getStorage() {
        return storage;
    }
    
    public void setStorage(String storage) {
        this.storage = storage;
    }
    
    public String getStatus() {
        return status;
    }
    
    @JSONSetter(name = "status")
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getScriptLine() {
        return scriptLine;
    }
    
    public void setScriptLine(String scriptLine) {
        this.scriptLine = scriptLine;
    }
    
    public Long getCompleted() {
        return completed;
    }
    
    public void setCompleted(Long completed) {
        this.completed = completed;
    }
    
    public String getNzoId() {
        return nzoId;
    }
    
    @JSONSetter(name = "nzo_id")
    public void setNzoId(String nzoId) {
        this.nzoId = nzoId;
    }
    
    public Long getDownloaded() {
        return downloaded;
    }
    
    public void setDownloaded(Long downloaded) {
        this.downloaded = downloaded;
    }
    
    public String getReport() {
        return report;
    }
    
    public void setReport(String report) {
        this.report = report;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public Integer getPostprocTime() {
        return postprocTime;
    }
    
    public void setPostprocTime(Integer postprocTime) {
        this.postprocTime = postprocTime;
    }
    
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
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Long getBytes() {
        return bytes;
    }
    
    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }
    
    public String getUrlInfo() {
        return urlInfo;
    }
    
    public void setUrlInfo(String urlInfo) {
        this.urlInfo = urlInfo;
    }
    
    public List<DownloadLog> getStageLog() {
        return stageLogs;
    }
    
    @JSONSetter(name="stage_log", type=JSONType.LIST, objectClazz=DownloadLog.class)
    public void setStageLog(List<DownloadLog> stageLogs) {
        this.stageLogs = stageLogs;
    }
}