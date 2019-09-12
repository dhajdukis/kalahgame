package hu.hajdukis.kalahgame.dto;

import java.util.Map;

public class GameStatusDto {
    private Integer startPit;
    private Map<Integer, Integer> statusMap;
    private Integer numberOfStones;
    private final boolean isFirstPlayer;

    public GameStatusDto(
            final Integer startPit,
            final Map<Integer, Integer> statusMap,
            final Integer numberOfStones,
            final Boolean isFirstPlayer) {
        this.startPit = startPit;
        this.statusMap = statusMap;
        this.numberOfStones = numberOfStones;
        this.isFirstPlayer = isFirstPlayer;
    }

    public void setStartPit(final Integer startPit) {
        this.startPit = startPit;
    }

    public void setStatusMap(final Map<Integer, Integer> statusMap) {
        this.statusMap = statusMap;
    }

    public void setNumberOfStones(final Integer numberOfStones) {
        this.numberOfStones = numberOfStones;
    }

    public Integer getStartPit() {
        return startPit;
    }

    public Map<Integer, Integer> getStatusMap() {
        return statusMap;
    }

    public Integer getNumberOfStones() {
        return numberOfStones;
    }

    public boolean isFirstPlayer() {
        return isFirstPlayer;
    }
}

