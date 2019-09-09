package hu.hajdukis.kalahgame.service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.hajdukis.kalahgame.dto.GameDto;
import hu.hajdukis.kalahgame.entity.Result;
import hu.hajdukis.kalahgame.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final ResultRepository resultRepository;

    @Autowired
    GameService(final ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Value("${server.port}")
    private String serverPort;

    @Transactional
    public GameDto startGame(final String clientIp) throws IOException {

        final Map<String, String> startMap = new LinkedHashMap<>();
        for (int i = 1; i <= 14; i++) {
            if (i != 7 && i != 14) {
                startMap.put(String.valueOf(i), "6");
            } else {
                startMap.put(String.valueOf(i), "0");
            }
        }

        //Who starts the game will be the first player
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

    @Transactional
    public GameDto makeAMove(final String gameId, final String pitId, final String clientIp) throws IOException {
        //Validations
        final Result result = resultRepository.findById(Long.parseLong(gameId)).orElseThrow(() -> new NoResultException(
                String.format("No game with the given id: %s", gameId)));

        if (!(clientIp).equals(result.getActualPlayersAddress())) {
            throw new IllegalArgumentException("It's not your turn!");
        }

        final int pit;
        try {
            pit = Integer.parseInt(pitId);
        } catch (final NumberFormatException ex) {
            throw new IllegalArgumentException(String.format("PitId [%s] cannot be parsed as an Integer!", pitId));
        }

        if (pit < 1 || pit > 13) {
            throw new IllegalArgumentException("PitId must be between '1' and '13'!");
        }

        if (pit == 7) {
            throw new IllegalArgumentException("PitId cannot be '7'!");
        }

        if ((clientIp.equals(result.getFirstPlayersAddress()) && pit > 6)
            || (clientIp.equals(result.getSecondPlayersAddress()) && pit <= 6)) {
            throw new IllegalArgumentException("The selected pit is your opponent's pit!");
        }

        final ObjectMapper mapper = new ObjectMapper();
        final Map<String, String> statusMap = mapper.readValue(result.getStatusJson(),
                                                               new TypeReference<LinkedHashMap<String, String>>() {});

        if ("0".equals(statusMap.get(pitId))) {
            throw new IllegalArgumentException(String.format("Illegal move: %s. pit is empty", pitId));
        }

        //Move logic
        return new GameDto(gameId,
                           "http://" + InetAddress.getLocalHost().getHostName() + ":" + serverPort + "/games" + "/id",
                           statusMap);
    }

    public byte[] statusJson(final Map status) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(status);
    }
}
