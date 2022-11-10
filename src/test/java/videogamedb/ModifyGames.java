package videogamedb;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class ModifyGames {
    static FeederBuilder<String> newGamesDataFeeder = csv("testdata/newGamesData.csv").circular();
    static ChainBuilder createNewGame = feed(newGamesDataFeeder)
            .exec(http("Create video game #{name}")
                    .post("/api/videogame")
                    .headers(Headers.authorizationHeaders)
                    .body(ElFileBody("inputdata/createGame.json"))
                    .check(jsonPath("$.name").isEL("#{name}"))
                    .check(jsonPath("$.reviewScore").isEL("#{reviewScore}")));

    static ChainBuilder updateGame = exec(http("Update a video game")
            .put("/api/videogame/10")
            .headers(Headers.authorizationHeaders)
            .body(RawFileBody("inputdata/updateGame.json"))
            .check(jsonPath("$.name").is("Marco")));

    static ChainBuilder deleteGame = exec(http("Delete a video game")
            .delete("/api/videogame/1")
            .headers(Headers.authorizationHeaders)
            .check(substring("Video game deleted")));
}
