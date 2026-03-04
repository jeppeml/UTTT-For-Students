package dk.easv.bll.bot;

import com.google.gson.Gson;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;

/**
 * REST client that communicates with the UTTT bot server.
 * Requires a VPN connection to the EASV network.
 *
 * The server hosts multiple bots at different strength levels.
 * Change DEFAULT_SLUG to play against a different bot:
 *   "teacher"      - Teacher MCTS (strength 2.0)
 *   "r16linrave"   - Teacher R16 LinRave (strength 3.0)
 *   "r19bitreuse"  - Teacher R19 BitReuse (strength 3.5)
 *   "r20multiroll" - Teacher R20 MultiRoll (strength 4.0)
 *
 * This client requires the Google Gson library.
 */
public class TeacherBotRESTv2 implements IBot {

    private static final String SERVER_URL = "http://10.176.88.89:4567";
    private static final String DEFAULT_SLUG = "teacher";

    private final String sessionId = UUID.randomUUID().toString();
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public IMove doMove(IGameState state) {
        String jsonState = gson.toJson(state);

        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(SERVER_URL + "/doMove/" + DEFAULT_SLUG))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("X-Session-Id", sessionId)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonState))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            return gson.fromJson(response.body(), Move.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Connection problem with " + DEFAULT_SLUG, e);
        }
    }

    @Override
    public String getBotName() {
        return DEFAULT_SLUG + " (REST)";
    }

    @Override
    public boolean isNetworkBot() { return true; }
}
