package hu.hajdukis.kalahgame.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import hu.hajdukis.kalahgame.dto.GameDto;
import hu.hajdukis.kalahgame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private static final String[] IP_HEADER_CANDIDATES = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"};
    private final GameService gameService;

    @Autowired
    GameController(final GameService gameService) {this.gameService = gameService;}

    @RequestMapping(value = "/games", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public GameDto startGame(final HttpServletRequest request) throws IOException {
        return gameService.startGame(getClientIpAddress(request));
    }

    @RequestMapping(value = "/games/{gameId}/pits/{pitId}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public GameDto makeAMove(
            @PathVariable("gameId") final String id,
            @PathVariable("pitId") final String pitId,
            final HttpServletRequest request) throws IOException {
        return gameService.makeAMove(id, pitId, getClientIpAddress(request));
    }

    public String getClientIpAddress(final HttpServletRequest request) {
        for (final String header : IP_HEADER_CANDIDATES) {
            final String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
