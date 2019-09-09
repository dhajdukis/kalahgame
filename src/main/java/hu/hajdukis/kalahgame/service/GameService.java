package hu.hajdukis.kalahgame.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import hu.hajdukis.kalahgame.dto.GameDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    @Value("${server.port}")
    private String serverPort;

    public GameDto startGame() throws UnknownHostException {

        return new GameDto("id",
                           "http://" + InetAddress.getLocalHost().getHostName() + ":" + serverPort + "/games" + "/id",
                           null);
    }
}
