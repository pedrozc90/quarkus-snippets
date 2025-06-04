package org.pedrozc90.adapters.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.Response.Status;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class RootResourceTest {

    @Test
    @DisplayName("GET /health returns a string")
    public void getHealthCheck() {
        given()
            .when()
            .get("/health")
            .then()
            .statusCode(Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .body("app", is("publisher"))
            .body("online", is(true))
            .body("mode", is("test"))
            .body("timestamp", anything())
            .body("timezone", is("America/Sao_Paulo"));
    }

}
