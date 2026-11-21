package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    private static final String NOMBRE = "Manuel Heber Sejuro Alfaro";
    private static final String CODIGO = "I202503691";
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Before
    public void beforeScenario() {
        System.out.println("=================================================");
        System.out.println("Estudiante: " + NOMBRE);
        System.out.println("Codigo: " + CODIGO);
        System.out.println("Fecha y hora: " + LocalDateTime.now().format(FORMATO));
        System.out.println("=================================================");
        OnStage.setTheStage(new OnlineCast());
    }
}
