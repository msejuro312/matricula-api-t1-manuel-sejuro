package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

public class ElCodigoDeError {

    public static Question<String> deLaRespuesta() {
        return Question.about("el codigo de error")
                .answeredBy(actor ->
                        SerenityRest.lastResponse()
                                .jsonPath()
                                .getString("codigo"));
    }
}