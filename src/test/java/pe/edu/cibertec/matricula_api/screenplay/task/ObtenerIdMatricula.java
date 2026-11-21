package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import java.util.List;
import java.util.Map;

public class ObtenerIdMatricula implements Task {

    private final String codigoCurso;
    private final String estado;

    private ObtenerIdMatricula(String codigoCurso, String estado) {
        this.codigoCurso = codigoCurso;
        this.estado = estado;
    }

    public static ObtenerIdMatricula delCurso(String codigoCurso) {
        return new ObtenerIdMatricula(codigoCurso, null);
    }

    public static ObtenerIdMatricula delCursoEnEstado(
            String codigoCurso, String estado) {
        return new ObtenerIdMatricula(codigoCurso, estado);
    }

    @Override
    @Step("{0} obtiene el id de su matricula del curso #codigoCurso")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Get.resource("/api/matriculas")
                        .with(req -> req.header(
                                "Authorization", "Bearer " + token))
        );

        Response respuesta = SerenityRest.lastResponse();
        List<Map<String, Object>> matriculas =
                respuesta.jsonPath().getList("$");

        Map<String, Object> matricula = matriculas.stream()
                .filter(m -> codigoCurso.equals(m.get("codigoCurso")))
                .filter(m -> estado == null || estado.equals(m.get("estado")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontro la matricula del curso " + codigoCurso
                                + (estado == null ? "" : " en estado " + estado)));

        long id = ((Number) matricula.get("id")).longValue();

        actor.remember("idMatricula", id);
        actor.remember("idMatricula-" + codigoCurso, id);
    }
}