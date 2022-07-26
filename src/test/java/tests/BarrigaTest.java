package tests;

import com.github.javafaker.Faker;
import core.BaseTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {
    Faker faker = new Faker();
    private String TOKEN;
    //private Map<String, String> nome = new HashMap<>();
    private static String CONTA_NAME;
    private static Integer CONTA_ID;

    @Before
    public void before(){
        Map<String, String> login = new HashMap<>();

        CONTA_NAME = faker.name().fullName();

        login.put("email", "wagner@aquino");
        login.put("senha", "123456");

        TOKEN = given()
                    .contentType(ContentType.JSON)
                    .relaxedHTTPSValidation()
                    .body(login)
                .when()
                .   post("/signin")
                .then()
                    .statusCode(200)
                    .extract().path("token")
                ;
    }

    @Test
    public void t01_naoDeveAcessarAPISemToken(){
        given()
        .when()
            .get("/contas")
        .then()
            .statusCode(401)
        ;
    }

    @Test
    public void t02_deveIncluirContaComSucesso(){
        CONTA_ID = given()
            .header("Authorization", "JWT " + TOKEN )
            .body("{\n" +
                    "    \"nome\": \""+CONTA_NAME+"\"\n" +
                    "}")
        .when()
            .post("/contas")
        .then()
            .statusCode(201)
            .extract().path("id")
        ;

    }

    @Test
    public void t03_deveAlterarContaComSucesso(){

        given()
            .header("Authorization", "JWT " + TOKEN )
            .body("{\n" +
                    "    \"nome\": \""+CONTA_NAME+"\"\n" +
                    "}")
            .pathParams("id", CONTA_ID)
        .when()
            .put("/contas/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("nome", is(CONTA_NAME))
        ;
    }





}
