package com.bcp.workshop.auth;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class AuthResourceTest {
    @Test
    void logsInWithWorkshopUser() {
        given()
                .contentType("application/json")
                .body("{\"username\":\"student\",\"password\":\"Workshop123!\",\"client\":\"angular-workshop\"}")
                .when().post("/auth/login")
                .then()
                .statusCode(200)
                .body("user.username", is("student"))
                .body("token", notNullValue());
    }

    @Test
    void rejectsSqlInjectionLikeUsername() {
        given()
                .contentType("application/json")
                .body("""
                        {"username":"student' OR '1'='1' -- ","password":"invalid"}
                        """)
                .when().post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    void rejectsLoginWithoutUsername() {
        given()
                .contentType("application/json")
                .body("{\"password\":\"invalid\"}")
                .when().post("/auth/login")
                .then()
                .statusCode(400);
    }

    @Test
    void rejectsLoginWithoutPassword() {
        given()
                .contentType("application/json")
                .body("{\"username\":\"student\"}")
                .when().post("/auth/login")
                .then()
                .statusCode(400);
    }
}
