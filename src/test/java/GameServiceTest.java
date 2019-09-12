import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.persistence.NoResultException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.hajdukis.kalahgame.entity.Result;
import hu.hajdukis.kalahgame.repository.ResultRepository;
import hu.hajdukis.kalahgame.service.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GameServiceTest {

    private GameService gameService;

    @Mock
    ResultRepository resultRepository;

    @BeforeEach
    void setup() {
        gameService = new GameService(resultRepository);
    }

    @Test
    void getStartMap() {

        final Map<String, String> referenceMap = new LinkedHashMap<>();
        referenceMap.put("1", "6");
        referenceMap.put("2", "6");
        referenceMap.put("3", "6");
        referenceMap.put("4", "6");
        referenceMap.put("5", "6");
        referenceMap.put("6", "6");
        referenceMap.put("7", "0");
        referenceMap.put("8", "6");
        referenceMap.put("9", "6");
        referenceMap.put("10", "6");
        referenceMap.put("11", "6");
        referenceMap.put("12", "6");
        referenceMap.put("13", "6");
        referenceMap.put("14", "0");

        final Map<String, String> startMap = gameService.getStartMap();
        Assertions.assertEquals(referenceMap, startMap);
    }

    @Test
    void no_game_with_the_given_id() {

        when(resultRepository.findById(1L)).thenReturn(Optional.empty());

        final Exception exception = Assertions.assertThrows(NoResultException.class,
                                                            () -> gameService.makeAMove(1, 1, "1"));
        Assertions.assertEquals("No game with the given id: 1!", exception.getMessage());
    }

    @Test
    void its_not_the_actual_players_turn() {

        final Result result = new Result("firstPlayersIP", "firstPlayersIP", null);
        when(resultRepository.findById(1L)).thenReturn(Optional.of(result));

        final Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                                                            () -> gameService.makeAMove(1, 1, "secondPlayersIP"));
        Assertions.assertEquals("It's not your turn!", exception.getMessage());
    }

    @Test
    void pitId_is_more_than_13() {

        final Result result = new Result("firstPlayersIP", "firstPlayersIP", null);
        when(resultRepository.findById(1L)).thenReturn(Optional.of(result));

        final Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gameService.makeAMove(1, 15, "firstPlayersIP"));
        Assertions.assertEquals("PitId must be between '1' and '13'!", exception.getMessage());
    }

    @Test
    void pitId_is_less_than_1() {

        final Result result = new Result("firstPlayersIP", "firstPlayersIP", null);
        when(resultRepository.findById(1L)).thenReturn(Optional.of(result));

        final Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                                                            () -> gameService.makeAMove(1, -1, "firstPlayersIP"));
        Assertions.assertEquals("PitId must be between '1' and '13'!", exception.getMessage());
    }

    @Test
    void pitId_is_7() {

        final Result result = new Result("firstPlayersIP", "firstPlayersIP", null);
        when(resultRepository.findById(1L)).thenReturn(Optional.of(result));

        final Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gameService.makeAMove(1, 7, "firstPlayersIP"));
        Assertions.assertEquals("PitId cannot be '7'!", exception.getMessage());
    }

    @Test
    void opponents_pit_was_selected() {

        final Result result = new Result("firstPlayersIP", "firstPlayersIP", null);
        when(resultRepository.findById(1L)).thenReturn(Optional.of(result));

        final Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                                                            () -> gameService.makeAMove(1, 12, "firstPlayersIP"));
        Assertions.assertEquals("The selected pit is your opponent's pit!", exception.getMessage());
    }

    @Test
    void the_selected_pit_is_empty() throws JsonProcessingException {

        final Map<String, String> status = new LinkedHashMap<>();
        status.put("1", "0");

        final ObjectMapper mapper = new ObjectMapper();
        final byte[] bytes = mapper.writeValueAsBytes(status);

        final Result result = new Result("firstPlayersIP", "firstPlayersIP", null);
        result.setStatusJson(bytes);

        when(resultRepository.findById(1L)).thenReturn(Optional.of(result));

        final Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                                                            () -> gameService.makeAMove(1, 1, "firstPlayersIP"));
        Assertions.assertEquals("The selected pit: 1 is empty!", exception.getMessage());
    }
}
