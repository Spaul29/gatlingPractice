package videogamedb;

import java.time.Duration;
import java.util.*;

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

        Map<CharSequence, String> authenticationHeader = new HashMap<>();
        authenticationHeader.put("authorization", "Bearer #{jwt}");

        FeederBuilder<String> existingGamesDataFeeder = csv("testdata/existingGamesData.csv").circular();
        FeederBuilder<String> newGamesDataFeeder = csv("testdata/newGamesData.csv").circular();

        ChainBuilder authenticate = exec(http("Authentication request")
                .post("/api/authenticate")
                .body(RawFileBody("inputdata/authenticate.json"))
                .check(status().is(200))
                .check(jsonPath("$.token").saveAs("jwt")));

        ChainBuilder listAllGames = exec(http("List all video games")
                .get("/api/videogame")
                .check(jsonPath("$[0].name").is("Resident Evil 4")));

        ChainBuilder listSpecificGame = feed(existingGamesDataFeeder)
                .exec( http("List a video game with id - #{id}")
                .get("/api/videogame/#{id}")
                .check(jsonPath("$.name").isEL("#{name}")));

        ChainBuilder createNewGame = feed(newGamesDataFeeder)
                .exec(http("Create video game #{name}")
                .post("/api/videogame")
                .headers(authenticationHeader)
                .body(ElFileBody("inputdata/createGame.json"))
                .check(jsonPath("$.name").isEL("#{name}"))
                .check(jsonPath("$.reviewScore").isEL("#{reviewScore}")));

        ChainBuilder updateGame = exec(http("Update a video game")
                .put("/api/videogame/10")
                .headers(authenticationHeader)
                .body(RawFileBody("inputdata/updateGame.json"))
                .check(jsonPath("$.name").is("Marco")));

        ChainBuilder deleteGame = exec(http("Delete a video game")
                .delete("/api/videogame/1")
                .headers(authenticationHeader)
                .check(substring("Video game deleted")));

        ScenarioBuilder scn = scenario("Video Game DB")
                .exec(listAllGames)
                .pause(minPause,maxPause)
                .repeat(10).on(exec(listSpecificGame))
                .pause(minPause,maxPause)
                .exec(authenticate)
                .pause(minPause,maxPause)
                .repeat(4).on(exec(createNewGame))
                .pause(minPause,maxPause)
                .exec(updateGame)
                .pause(minPause,maxPause)
                .exec(deleteGame);

        setUp(scn.injectOpen(rampUsers(userCount).during(rampDuration)))
                .maxDuration(maxDuration).protocols(httpProtocol);
    }
}
