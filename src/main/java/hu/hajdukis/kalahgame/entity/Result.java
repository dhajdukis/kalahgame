package hu.hajdukis.kalahgame.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RESULT")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private short playerId;

    public Result() {}

    public Result(final Long id, final short playerId) {
        this.id = id;
        this.playerId = playerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public short getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final short playerId) {
        this.playerId = playerId;
    }
}
