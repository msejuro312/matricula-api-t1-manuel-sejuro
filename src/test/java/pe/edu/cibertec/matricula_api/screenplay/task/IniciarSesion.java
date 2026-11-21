package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

public class IniciarSesion implements Task {

    private final String username;
    private final String password;

    private IniciarSesion(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static IniciarSesion con(String username, String password) {
        return new IniciarSesion(username, password);
    }

    @Override
    @Step("{0} inicia sesion como #username")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/api/auth/login")
                        .with(req -> req
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "username", username,
                                        "password", password)))
        );

        Response respuesta = SerenityRest.lastResponse();

        if (respuesta.statusCode() == 200) {
            actor.remember("accessToken",
                    respuesta.jsonPath().getString("accessToken"));
            actor.remember("refreshToken",
                    respuesta.jsonPath().getString("refreshToken"));
            actor.remember("username",
                    respuesta.jsonPath().getString("username"));
            actor.remember("rol",
                    respuesta.jsonPath().getString("rol"));
        }
    }
}