package hu.hajdukis.kalahgame.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "RESULT")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstPlayersAddress;
    private String secondPlayersAddress;
    private String actualPlayersAddress;
    @Lob
    private byte[] statusJson;

    public Result() {}

    public Result(
            final String firstPlayersAddress, final String actualPlayersAddress, final byte[] statusJson) {
        this.firstPlayersAddress = firstPlayersAddress;
        this.actualPlayersAddress = actualPlayersAddress;
        this.statusJson = statusJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFirstPlayersAddress() {
        return firstPlayersAddress;
    }

    public void setFirstPlayersAddress(final String firstPlayersAddress) {
        this.firstPlayersAddress = firstPlayersAddress;
    }

    public String getSecondPlayersAddress() {
        return secondPlayersAddress;
    }

    public void setSecondPlayersAddress(final String secondPlayersAddress) {
        this.secondPlayersAddress = secondPlayersAddress;
    }

    public String getActualPlayersAddress() {
        return actualPlayersAddress;
    }

    public void setActualPlayersAddress(final String actualPlayersAddress) {
        this.actualPlayersAddress = actualPlayersAddress;
    }

    public byte[] getStatusJson() {
        return statusJson;
    }

    public void setStatusJson(final byte[] statusJson) {
        this.statusJson = statusJson;
    }
}
