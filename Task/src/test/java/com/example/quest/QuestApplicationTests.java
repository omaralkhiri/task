package com.example.quest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "YOUR_NAME=explorer")
class QuestApplicationTests {
    @LocalServerPort int port;

    private HttpResponse<String> get(String path) throws Exception {
        return HttpClient.newHttpClient().send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).build(),
            HttpResponse.BodyHandlers.ofString());
    }

    @Test void greetingUsesConfiguredNameAndPlainText() throws Exception {
        var response = get("/");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("hello explorer");
        assertThat(response.headers().firstValue("content-type").orElse("")).startsWith("text/plain");
    }
    @Test void healthIsAvailableWithoutDetails() throws Exception {
        var response = get("/actuator/health");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"status\":\"UP\"").doesNotContain("components", "details");
    }
    @Test void sensitiveActuatorEndpointsAreNotExposed() throws Exception {
        assertThat(get("/actuator/env").statusCode()).isEqualTo(404);
        assertThat(get("/actuator/heapdump").statusCode()).isEqualTo(404);
    }
    @Test void blankAndMissingNamesHaveSafeDefaults() {
        assertThat(new GreetingController(" ").greeting()).isEqualTo("hello traveler");
        assertThat(new GreetingController("traveler").greeting()).isEqualTo("hello traveler");
    }
}
