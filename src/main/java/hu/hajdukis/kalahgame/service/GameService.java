package hu.hajdukis.kalahgame.service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.NoResultException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.hajdukis.kalahgame.dto.GameDto;
import hu.hajdukis.kalahgame.dto.GameStatusDto;
import hu.hajdukis.kalahgame.entity.Result;
import hu.hajdukis.kalahgame.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {

    private final ResultRepository resultRepository;

    public GameService(final ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Value("${server.port}")
    private String serverPort;

    @Transactional(rollbackFor = Exception.class)
    public GameDto startGame(final String clientIp) throws IOException {

        final Map<String, String> startMap = getStartMap();

        final Result result = new Result(clientIp, clientIp, statusJson(startMap));
        final String savedEntityId = resultRepository.save(result).getId().toString();

        return new GameDto(savedEntityId,
                           "http://"
                           + InetAddress.getLocalHost().getHostName()
                           + ":"
                           + serverPort
                           + "/games/"
                           + savedEntityId,
                           null);
    }

    public Map<String, String> getStartMap() {
        final Map<String, String> startMap = new LinkedHashMap<>();
        for (int i = 1; i <= 14; i++) {
            if (i != 7 && i != 14) {
                startMap.put(String.valueOf(i), "6");
            } else {
                startMap.put(String.valueOf(i), "0");
            }
        }
        return startMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public GameDto makeAMove(final Integer gameId, final Integer pitId, final String clientIp) {

        final Result result = resultRepository.findById(gameId.longValue()).orElseThrow(() -> new NoResultException(
                String.format("No game with the given id: %s!", gameId)));

        if ((result.getActualPlayersAddress() == null && (clientIp).equals(result.getFirstPlayersAddress()))
            || (!(clientIp).equals(result.getActualPlayersAddress()) && result.getActualPlayersAddress() != null)) {
            throw new IllegalArgumentException("It's not your turn!");
        }

        if (result.getActualPlayersAddress() == null) {
            result.setSecondPlayersAddress(clientIp);
            result.setActualPlayersAddress(clientIp);
        }

        checkPitId(pitId);

        if ((clientIp.equals(result.getFirstPlayersAddress()) && pitId > 6)
            || (clientIp.equals(result.getSecondPlayersAddress()) && pitId <= 6)) {
            throw new IllegalArgumentException("The selected pit is your opponent's pit!");
        }

        Map<Integer, Integer> statusMap;
        try {
            statusMap = getStatusMap(result);
        } catch (final IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        if (statusMap.get(pitId) == 0) {
            throw new IllegalArgumentException(String.format("The selected pit: %s is empty!", pitId));
        }

        final Integer numberOfStones = statusMap.get(pitId);
        final GameStatusDto gameStatusDto = new GameStatusDto(pitId,
                                                              statusMap,
                                                              numberOfStones,
                                                              clientIp.equals(result.getFirstPlayersAddress()));

        statusMap = stepsIterator(gameStatusDto).getStatusMap();

        final Map<Integer, String> resultMap = statusMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                      e -> Integer.toString(
                                                                                                              e.getValue())));
        final String serverHostname;
        try {
            result.setStatusJson(statusJson(resultMap));
            serverHostname = InetAddress.getLocalHost().getHostName();
        } catch (final IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (result.getSecondPlayersAddress() == null) {
            result.setActualPlayersAddress(null);
        }

        return new GameDto(gameId.toString(),
                           "http://" + serverHostname + ":" + serverPort + "/games" + "/id",
                           resultMap);
    }

    public GameStatusDto stepsIterator(
            GameStatusDto gameStatusDto) {
        final Map<Integer, Integer> statusMap = gameStatusDto.getStatusMap();
        Integer pitId = gameStatusDto.getStartPit();
        Integer numberOfStones = gameStatusDto.getNumberOfStones();
        statusMap.replace(pitId, 0);

        do {
            pitId++;
            if ((!gameStatusDto.isFirstPlayer() && pitId == 7) || (gameStatusDto.isFirstPlayer() && pitId == 14)) {
                continue;
            }
            statusMap.replace(pitId, statusMap.get(pitId) + 1);

            numberOfStones--;

            //TODO: capture only from opponents pit
            if (numberOfStones == 0) {
                if (statusMap.get(pitId) != 1 || ((!gameStatusDto.isFirstPlayer() || pitId != 7)
                                                  && (gameStatusDto.isFirstPlayer() || pitId != 14))) {
                    statusMap.replace(pitId, statusMap.get(14 - pitId) + 1);
                }
                break;
            }
        } while (pitId <= 13);

        gameStatusDto.setStatusMap(statusMap);
        gameStatusDto.setStartPit(0);
        gameStatusDto.setNumberOfStones(numberOfStones);

        if (numberOfStones == 0) {
            return gameStatusDto;
        } else {
            gameStatusDto = stepsIterator(gameStatusDto);
            return gameStatusDto;
        }
    }

    private byte[] statusJson(final Map status) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(status);
    }

    private Map<Integer, Integer> getStatusMap(final Result result) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result.getStatusJson(), new TypeReference<LinkedHashMap<Integer, Integer>>() {});
    }

    private void checkPitId(final Integer pitId) {
        if (pitId < 1 || pitId > 13) {
            throw new IllegalArgumentException("PitId must be between '1' and '13'!");
        }

        if (pitId == 7) {
            throw new IllegalArgumentException("PitId cannot be '7'!");
        }
    }
}
