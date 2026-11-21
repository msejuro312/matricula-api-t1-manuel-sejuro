package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

import java.util.List;
import java.util.Map;

public class ElHistorial {

    public static Question<Integer> creditosAprobados() {
        return Question.about("los creditos aprobados")
                .answeredBy(actor ->
                        SerenityRest.lastResponse()
                                .jsonPath()
                                .getInt("creditosAprobados"));
    }

    public static Question<Double> promedioPonderado() {
        return Question.about("el promedio ponderado")
                .answeredBy(actor ->
                        SerenityRest.lastResponse()
                                .jsonPath()
                                .getDouble("promedioPonderado"));
    }

    public static Question<String> condicionDelCurso(String codigoCurso) {
        return Question.about("la condicion del curso " + codigoCurso)
                .answeredBy(actor -> {
                    List<Map<String, Object>> cursos =
                            SerenityRest.lastResponse()
                                    .jsonPath()
                                    .getList("cursos");

                    return cursos.stream()
                            .filter(c -> codigoCurso.equals(c.get("codigoCurso")))
                            .map(c -> (String) c.get("condicion"))
                            .findFirst()
                            .orElse(null);
                });
    }
}