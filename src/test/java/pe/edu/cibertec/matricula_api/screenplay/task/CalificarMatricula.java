package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

public class CalificarMatricula implements Task {

    private final long id;
    private final double nota;

    private CalificarMatricula(long id, double nota) {
        this.id = id;
        this.nota = nota;
    }

    public static CalificarMatricula conId(long id) {
        return new CalificarMatricula(id, 0);
    }

    public CalificarMatricula conNota(double nota) {
        return new CalificarMatricula(id, nota);
    }

    @Override
    @Step("{0} califica la matricula #id con nota #nota")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Patch.to("/api/matriculas/{id}/nota")
                        .with(req -> req
                                .pathParam("id", id)
                                .header("Authorization", "Bearer " + token)
                                .contentType(ContentType.JSON)
                                .body(Map.of("nota", nota)))
        );
    }
}