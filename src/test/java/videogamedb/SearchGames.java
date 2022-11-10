package videogamedb;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SearchGames {

    static FeederBuilder<String> existingGamesDataFeeder = csv("testdata/existingGamesData.csv").circular();

    static ChainBuilder listAllGames = exec(http("List all video games")
            .get("/api/videogame")
            .check(jsonPath("$[0].name").is("Resident Evil 4")));

    static ChainBuilder listSpecificGame = feed(existingGamesDataFeeder)
            .exec( http("List a video game with id - #{id}")
                    .get("/api/videogame/#{id}")
                    .check(jsonPath("$.name").isEL("#{name}")));
}
