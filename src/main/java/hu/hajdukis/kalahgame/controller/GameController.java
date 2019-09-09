package hu.hajdukis.kalahgame.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

import hu.hajdukis.kalahgame.dto.GameDto;
import hu.hajdukis.kalahgame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @Value("${server.port}")
    private String serverPort;
    private final GameService gameService;

    @Autowired
    GameController(final GameService gameService) {this.gameService = gameService;}

    @RequestMapping(value = "/games", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public GameDto startGame() throws UnknownHostException {
        return gameService.startGame();
    }

    @RequestMapping(value = "/games/{gameId}/pits/{pitId}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public GameDto makeAMove(
            @PathVariable("gameId") final String id,
            @PathVariable("pitId") final String pitId) throws UnknownHostException {
        //move
        final Map result = new TreeMap();
        result.put(1, pitId);

        return new GameDto(id,
                           "http://" + InetAddress.getLocalHost().getHostName() + ":" + serverPort + "/games" + "/id",
                           result);
    }
}
