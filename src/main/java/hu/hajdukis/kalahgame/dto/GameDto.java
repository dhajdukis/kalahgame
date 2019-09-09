package hu.hajdukis.kalahgame.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

public class GameDto {
    private String id;
    private String url;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map status;

    public GameDto(final String id, final String url, final Map status) {
        this.id = id;
        this.url = url;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Map getStatus() {
        return status;
    }

    public void setStatus(final Map status) {
        this.status = status;
    }
}
