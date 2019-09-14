package hu.hajdukis.kalahgame.controller;

import java.io.IOException;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import hu.hajdukis.kalahgame.dto.GameDto;
import hu.hajdukis.kalahgame.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/games")
public class GameController {

    private static final String[] IP_HEADER_CANDIDATES = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"};

    private final GameService gameService;

    GameController(final GameService gameService) {this.gameService = gameService;}

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public GameDto startGame(final HttpServletRequest request) {
        try {
            return gameService.startGame(getClientIpAddress(request));
        } catch (final IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (final IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping(value = "/{gameId}/pits/{pitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public GameDto makeAMove(
            @PathVariable("gameId") final Integer id,
            @PathVariable("pitId") final Integer pitId,
            final HttpServletRequest request) {
        try {
            return gameService.makeAMove(id, pitId, getClientIpAddress(request));
        } catch (final IllegalArgumentException | NoResultException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (final IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private String getClientIpAddress(final HttpServletRequest request) {
        for (final String header : IP_HEADER_CANDIDATES) {
            final String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
