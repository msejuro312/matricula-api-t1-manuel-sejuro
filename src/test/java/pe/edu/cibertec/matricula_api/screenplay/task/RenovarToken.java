package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

public class RenovarToken implements Task {

    private final String refreshToken;

    private RenovarToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static RenovarToken conElRefreshActual() {
        return new RenovarToken(null);
    }

    public static RenovarToken con(String refreshToken) {
        return new RenovarToken(refreshToken);
    }

    @Override
    @Step("{0} renueva sus tokens")
    public <T extends Actor> void performAs(T actor) {
        String token = refreshToken != null
                ? refreshToken
                : actor.recall("refreshToken");

        if (refreshToken == null) {
            String accessTokenAnterior = actor.recall("accessToken");
            actor.remember("refreshTokenOriginal", token);
            actor.remember("accessTokenAnterior", accessTokenAnterior);
        }

        actor.attemptsTo(
                Post.to("/api/auth/refresh")
                        .with(req -> req
                                .contentType(ContentType.JSON)
                                .body(Map.of("refreshToken", token)))
        );

        Response respuesta = SerenityRest.lastResponse();

        if (respuesta.statusCode() == 200) {
            actor.remember("accessToken",
                    respuesta.jsonPath().getString("accessToken"));
            actor.remember("refreshToken",
                    respuesta.jsonPath().getString("refreshToken"));
        }
    }
}