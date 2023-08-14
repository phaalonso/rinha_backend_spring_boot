package com.example.demo;


import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.gatling.core.body.StringBody;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.json.GsonTester;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Disabled
public class ApiGatlingSimulationTest extends Simulation {
    Faker faker = new Faker();
    Gson gson = new Gson();
    HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl("http://127.0.0.1:8080/")
//            .baseUrl("http://127.0.0.1:9999/")
            .acceptHeader("application/json")
            .userAgentHeader("Gatling/Performance Test");
    // Scenario
    ScenarioBuilder scn = scenario("Load Test Creating Customers")
            .feed(() -> Stream.generate(() -> {
                        var name = faker.name();
                        var stack = List.of(faker.programmingLanguage().name());

                return Map.<String, Object>of(
                                "name", name.fullName(),
                                "nick", name.username(),
                                "birthDate", LocalDate.ofInstant(faker.date().birthday(20, 100).toInstant(), ZoneId.systemDefault()).toString(),
                                "stack", gson.toJson(stack)
                        );
                    }
            ).iterator())
            .exec(http("create user request")
                    .post("people")
                    .header("Content-Type", "application/json")
                    .body(StringBody("""
                                    {
                                       "name": "#{name}",
                                       "nick": "#{nick}",
                                       "birthDate": "#{birthDate}",
                                       "stack": #{stack}
                                    }
                                    """
                            )
                    )
                    .check(status().is(201))
                    .check(headerRegex("Location", "/people/(.*)")
                            .saveAs("findUser"))
            )
            .pause(1)
            .exec(http("find created user request")
                    .get("/people/#{findUser}")
                    .check(status().is(200))
            );

    {
        this.setUp(scn.injectOpen(rampUsersPerSec(100).to(1000).during(60)))
                .protocols(httpProtocol);
    }
}
