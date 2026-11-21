package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElHistorial;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.CalificarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarHistorial;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.ObtenerIdMatricula;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

public class CalificacionStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    @Given("que el docente {string} inicia sesion en la API")
    public void elDocenteIniciaSesion(String codigo) {
        theActorCalled("Docente").whoCan(CallAnApi.at(baseUrl));
        theActorCalled("Docente").attemptsTo(
                IniciarSesion.con(codigo, "Cibertec2026")
        );
        theActorCalled("Docente").should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(200))
        );
    }

    @Given("que la alumna {string} inicia sesion en la API")
    public void laAlumnaIniciaSesion(String codigo) {
        theActorCalled("Alumna").whoCan(CallAnApi.at(baseUrl));
        theActorCalled("Alumna").attemptsTo(
                IniciarSesion.con(codigo, "Cibertec2026")
        );
        theActorCalled("Alumna").should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(200))
        );
    }

    @Given("la alumna obtiene el id de su matricula del curso {string}")
    public void laAlumnaObtieneElIdDeSuMatricula(String curso) {
        theActorCalled("Alumna").attemptsTo(
                ObtenerIdMatricula.delCurso(curso)
        );
    }

    @Given("la alumna obtiene el id de su matricula del curso {string} en estado {string}")
    public void laAlumnaObtieneElIdDeSuMatriculaEnEstado(
            String curso, String estado) {
        theActorCalled("Alumna").attemptsTo(
                ObtenerIdMatricula.delCursoEnEstado(curso, estado)
        );
    }

    @When("el docente califica la matricula con la nota {double}")
    public void elDocenteCalificaLaMatricula(double nota) {
        Long id = theActorCalled("Alumna").recall("idMatricula");

        theActorCalled("Docente").attemptsTo(
                CalificarMatricula.conId(id).conNota(nota)
        );
    }

    @Then("la condicion de la matricula es {string}")
    public void laCondicionDeLaMatriculaEs(String condicion) {
        theActorCalled("Docente").should(
                seeThat(LaRespuesta.campo("condicion"), equalTo(condicion))
        );
    }

    @When("la alumna consulta su historial")
    public void laAlumnaConsultaSuHistorial() {
        String codigo = theActorCalled("Alumna").recall("username");

        theActorCalled("Alumna").attemptsTo(
                ConsultarHistorial.de(codigo)
        );
    }

    @Then("el historial tiene {int} creditos aprobados")
    public void elHistorialTieneCreditosAprobados(int creditos) {
        theActorInTheSpotlight().should(
                seeThat(ElHistorial.creditosAprobados(), equalTo(creditos))
        );
    }

    @Then("el promedio ponderado es {double}")
    public void elPromedioPonderadoEs(double promedio) {
        theActorInTheSpotlight().should(
                seeThat(ElHistorial.promedioPonderado(),
                        closeTo(promedio, 0.001))
        );
    }

    @Then("el curso {string} tiene condicion {string}")
    public void elCursoTieneCondicion(String curso, String condicion) {
        theActorInTheSpotlight().should(
                seeThat(ElHistorial.condicionDelCurso(curso),
                        equalTo(condicion))
        );
    }
}