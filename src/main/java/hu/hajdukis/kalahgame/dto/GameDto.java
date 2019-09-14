package hu.hajdukis.kalahgame.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

public class GameDto {
    private String id;
    private String uri;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map status;

    public GameDto(final String id, final String uri, final Map status) {
        this.id = id;
        this.uri = uri;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public Map getStatus() {
        return status;
    }

    public void setStatus(final Map status) {
        this.status = status;
    }
}
