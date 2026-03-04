package dk.easv.bll.bot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.io.IOException;
import java.net.ConnectException;
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
        String url = SERVER_URL + "/doMove/" + DEFAULT_SLUG;
        String jsonState;
        try {
            jsonState = gson.toJson(state);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize game state: " + e.getMessage(), e);
        }

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("X-Session-Id", sessionId)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonState))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, BodyHandlers.ofString());
        } catch (ConnectException e) {
            throw new RuntimeException("Cannot connect to " + url
                    + " — is the server running and reachable? (" + e.getMessage() + ")", e);
        } catch (IOException e) {
            throw new RuntimeException("Network error connecting to " + url + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Request to " + url + " was interrupted", e);
        }

        String body = response.body();

        if (response.statusCode() != 200) {
            String serverMsg = body;
            try {
                JsonObject obj = gson.fromJson(body, JsonObject.class);
                if (obj != null && obj.has("error")) {
                    serverMsg = obj.get("error").getAsString();
                }
            } catch (Exception ignored) {}
            throw new RuntimeException("Server returned HTTP " + response.statusCode() + ": " + serverMsg);
        }

        Move move;
        try {
            move = gson.fromJson(body, Move.class);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Invalid JSON from server: " + body, e);
        }

        if (move == null) {
            throw new RuntimeException("Server returned empty response (expected move JSON, got: " + body + ")");
        }

        if (move.getX() < 0 || move.getX() >= 9 || move.getY() < 0 || move.getY() >= 9) {
            throw new RuntimeException("Server returned out-of-bounds move: (" + move.getX() + "," + move.getY() + ")");
        }

        return move;
    }

    @Override
    public String getBotName() {
        return DEFAULT_SLUG + " (REST)";
    }

    @Override
    public boolean isNetworkBot() { return true; }
}
