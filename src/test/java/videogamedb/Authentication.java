package videogamedb;

import io.gatling.javaapi.core.ChainBuilder;

import java.util.HashMap;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class Authentication {

    static ChainBuilder authenticate = doIf(session -> !session.getBoolean("authenticated"))
            .then(exec(http("Authentication request")
                    .post("/api/authenticate")
                    .body(RawFileBody("inputdata/authenticate.json"))
                    .check(status().is(200))
                    .check(jsonPath("$.token").saveAs("jwt")))
                    .exec(session -> session.set("authenticated",true)));
}
