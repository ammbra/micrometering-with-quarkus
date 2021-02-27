package org.acme.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ExampleEndpointTest {


    @Test
    public void createGreeting() {
        given()
          .when().put("/api/v1/make/en/hello")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

    @Test
    public void findGreeting() {
        given()
                .when().get("/api/v1/find/bonjour")
                .then()
                .statusCode(200)
                .body(is("[Message(id=1000, locale=fr, content=bonjour)]"));
    }

    @Test
    public void findNonExistingGreeting() {
        given()
                .when().get("/api/v1/find/buna")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @Test
    public void findNonExistingLanguage() {
        given()
                .when().get("/api/v1/find/zz")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @Test
    public void findAll() {
        given()
                .when().get("/api/v1/find")
                .then()
                .statusCode(200) ;
    }
}