package io.openshift.example;

import io.restassured.RestAssured;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.restassured.RestAssured.get;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.setDefaultTimeout;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class CuteNameServiceVerticleTest {

    private final static int PORT = 8081;
    private Vertx vertx;

    @Before
    public void setUp() {
        vertx = Vertx.vertx();
        vertx.deployVerticle(CuteNameServiceVerticle.class.getName(),
            new DeploymentOptions().setConfig(new JsonObject().put("http.port", PORT)));

        RestAssured.baseURI = "http://localhost:" + PORT;
        setDefaultTimeout(1, TimeUnit.MINUTES);

        await().until(() -> {
            try {
                return get("/health").statusCode() == 200;
            } catch (Exception e) {
                return false;
            }
        });
    }

    @After
    public void tearDown() {
        AtomicBoolean closed = new AtomicBoolean();
        vertx.close(x -> closed.set(x.succeeded()));
        await().untilAtomic(closed, is(true));
    }

    @Test
    public void testHealth() {
        get("/health").then().statusCode(200);
    }

    @Test
    public void testWeGetName() {
        get("/api/name").then().statusCode(200).body("name", is(not(empty())));
    }


}