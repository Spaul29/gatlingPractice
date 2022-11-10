package videogamedb;

import java.time.Duration;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGame extends Simulation {

    {
        Duration minPause = Duration.ofSeconds(2);
        Duration maxPause = Duration.ofSeconds(4);

        int userCount = Integer.parseInt(System.getProperty("userCount","5"));
        Duration rampDuration = Duration.ofSeconds(Integer.parseInt(System.getProperty("rampDuration","60")));
        Duration maxDuration = Duration.ofSeconds(Integer.parseInt(System.getProperty("maxDuration","30")));

        HttpProtocolBuilder httpProtocol = http
                .baseUrl("https://videogamedb.uk")
                .acceptHeader("application/json")
                .contentTypeHeader("application/json");

        ChainBuilder initializeSession = exec(session -> session.set("authenticated",false));

        ScenarioBuilder scn = scenario("Video Game DB")
                .exec(initializeSession)
                .exec(SearchGames.listAllGames)
                .pause(minPause,maxPause)
                .repeat(10).on(exec(SearchGames.listSpecificGame))
                .pause(minPause,maxPause)
                .exec(Authentication.authenticate)
                .pause(minPause,maxPause)
                .repeat(4).on(exec(ModifyGames.createNewGame))
                .pause(minPause,maxPause)
                .exec(ModifyGames.updateGame)
                .pause(minPause,maxPause)
                .exec(ModifyGames.deleteGame);

        setUp(scn.injectOpen(rampUsers(userCount).during(rampDuration)))
                .maxDuration(maxDuration).protocols(httpProtocol);
    }
}
