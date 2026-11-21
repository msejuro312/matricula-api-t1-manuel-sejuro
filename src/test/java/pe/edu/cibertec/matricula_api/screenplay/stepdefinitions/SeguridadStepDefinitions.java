package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElCodigoDeError;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.CerrarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarPerfil;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.ListarAlumnos;
import pe.edu.cibertec.matricula_api.screenplay.task.RenovarToken;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SeguridadStepDefinitions {

    @And("inicia sesion en la API con el usuario {string} y la clave {string}")
    public void iniciaSesionEnLaApi(String usuario, String clave) {
        theActorInTheSpotlight().attemptsTo(
                IniciarSesion.con(usuario, clave)
        );
        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(200))
        );
    }

    @When("renueva sus tokens")
    public void renuevaSusTokens() {
        theActorInTheSpotlight().attemptsTo(
                RenovarToken.conElRefreshActual()
        );
    }

    @And("los tokens son nuevos")
    public void losTokensSonNuevos() {
        Actor actor = theActorInTheSpotlight();

        String accessAnterior = actor.recall("accessTokenAnterior");
        String accessNuevo = actor.recall("accessToken");
        String refreshOriginal = actor.recall("refreshTokenOriginal");
        String refreshNuevo = actor.recall("refreshToken");

        assertNotNull(accessNuevo, "No se obtuvo el access token nuevo");
        assertNotNull(refreshNuevo, "No se obtuvo el refresh token nuevo");
        assertNotEquals(accessAnterior, accessNuevo,
                "El access token no cambio");
        assertNotEquals(refreshOriginal, refreshNuevo,
                "El refresh token no cambio");
    }

    @When("intenta renovar nuevamente con el refresh token original")
    public void intentaRenovarConElRefreshOriginal() {
        Actor actor = theActorInTheSpotlight();
        String refreshOriginal = actor.recall("refreshTokenOriginal");

        actor.attemptsTo(
                RenovarToken.con(refreshOriginal)
        );
    }

    @When("consulta su perfil con el token actual")
    public void consultaSuPerfilConElTokenActual() {
        theActorInTheSpotlight().attemptsTo(
                ConsultarPerfil.actual()
        );
    }

    @When("cierra su sesion")
    public void cierraSuSesion() {
        theActorInTheSpotlight().attemptsTo(
                CerrarSesion.actual()
        );
    }

    @When("intenta listar todos los alumnos")
    public void intentaListarTodosLosAlumnos() {
        theActorInTheSpotlight().attemptsTo(
                ListarAlumnos.todos()
        );
    }

    @When("intenta iniciar sesion con estas credenciales:")
    public void intentaIniciarSesionConEstasCredenciales(DataTable tabla) {
        List<Map<String, String>> filas =
                tabla.asMaps(String.class, String.class);

        for (Map<String, String> fila : filas) {
            int status = Integer.parseInt(fila.get("status"));
            String codigo = fila.get("codigo");

            theActorInTheSpotlight().attemptsTo(
                    IniciarSesion.con(
                            fila.get("usuario"),
                            fila.get("clave"))
            );

            theActorInTheSpotlight().should(
                    seeThat(LaRespuesta.codigoDeEstado(), equalTo(status)),
                    seeThat(ElCodigoDeError.deLaRespuesta(), equalTo(codigo))
            );

            SerenityRest.lastResponse()
                    .then()
                    .body(matchesJsonSchemaInClasspath("schemas/error.json"));
        }
    }

    @When("intenta iniciar sesion con el usuario {string} y la clave {string}")
    public void intentaIniciarSesionConElUsuarioYLaClave(
            String usuario, String clave) {

        theActorInTheSpotlight().attemptsTo(
                IniciarSesion.con(usuario, clave)
        );
    }
}