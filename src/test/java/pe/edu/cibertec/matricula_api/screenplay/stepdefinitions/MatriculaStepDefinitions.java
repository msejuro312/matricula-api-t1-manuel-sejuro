package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.environment.SystemEnvironmentVariables;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatriculaStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    private String accessToken;
    private String codigoAlumno;
    private long matriculaId;
    private String numeroOperacion;

    @And("inicia sesion con el usuario {string} y la clave {string}")
    public void iniciaSesion(String usuario, String clave) {
        Response respuesta = SerenityRest.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(Map.of("username", usuario, "password", clave))
                .post("/api/auth/login");

        assertEquals(200, respuesta.statusCode(), "No se pudo iniciar sesion");
        accessToken = respuesta.jsonPath().getString("accessToken");
        codigoAlumno = usuario;
        assertNotNull(accessToken, "El login no devolvio accessToken");
    }

    @When("cotiza el curso {string}")
    public void cotizaElCurso(String curso) {
        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(Map.of("codigoAlumno", codigoAlumno, "cursos", List.of(curso)))
                .post("/api/matriculas/cotizacion");
    }

    @When("cotiza los cursos {string}")
    public void cotizaLosCursos(String cursos) {
        List<String> lista = Arrays.stream(cursos.split(","))
                .map(String::trim)
                .toList();

        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(Map.of("codigoAlumno", codigoAlumno, "cursos", lista))
                .post("/api/matriculas/cotizacion");
    }

    @And("la cotizacion tiene {int} creditos")
    public void laCotizacionTieneCreditos(int creditos) {
        assertEquals(creditos, SerenityRest.lastResponse().jsonPath().getInt("creditos"));
    }

    @And("el subtotal de la cotizacion es {double}")
    public void elSubtotalDeLaCotizacionEs(double subtotal) {
        assertEquals(subtotal,
                SerenityRest.lastResponse().jsonPath().getDouble("subtotal"), 0.001);
    }

    @And("el descuento de la cotizacion es {int}")
    public void elDescuentoDeLaCotizacionEs(int descuento) {
        assertEquals(descuento,
                SerenityRest.lastResponse().jsonPath().getInt("descuento"));
    }

    @And("el total de la cotizacion es {double}")
    public void elTotalDeLaCotizacionEs(double total) {
        assertEquals(total,
                SerenityRest.lastResponse().jsonPath().getDouble("total"), 0.001);
    }

    @When("se matricula en el curso {string}")
    public void seMatriculaEnElCurso(String curso) {
        Response respuesta = SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(Map.of("codigoAlumno", codigoAlumno, "codigoCurso", curso))
                .post("/api/matriculas");

        if (respuesta.statusCode() == 201) {
            matriculaId = respuesta.jsonPath().getLong("id");
        }
    }

    @And("el estado de la matricula es {string}")
    public void elEstadoDeLaMatriculaEs(String estado) {
        assertEquals(estado,
                SerenityRest.lastResponse().jsonPath().getString("estado"));
    }

    @And("el monto de la matricula es {double}")
    public void elMontoDeLaMatriculaEs(double monto) {
        assertEquals(monto,
                SerenityRest.lastResponse().jsonPath().getDouble("monto"), 0.001);
    }

    @When("paga la matricula con el metodo {string}")
    public void pagaLaMatriculaConElMetodo(String metodo) {
        assertTrue(matriculaId > 0, "No se obtuvo el id de la matricula");

        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(Map.of("metodo", metodo))
                .patch("/api/matriculas/" + matriculaId + "/pagar");
    }

    @And("la fecha de pago no es nula")
    public void laFechaDePagoNoEsNula() {
        String fechaPago = SerenityRest.lastResponse().jsonPath().getString("fechaPago");
        assertNotNull(fechaPago, "La fecha de pago no debe ser nula");
        assertFalse(fechaPago.isBlank(), "La fecha de pago no debe estar vacia");
    }

    @When("consulta los pagos del alumno")
    public void consultaLosPagosDelAlumno() {
        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .get("/api/pagos");
    }

    @Then("existe el pago de la matricula con el metodo {string}")
    public void existeElPagoDeLaMatriculaConElMetodo(String metodo) {
        List<Map<String, Object>> pagos = SerenityRest.lastResponse().jsonPath().getList("$");

        Map<String, Object> pago = pagos.stream()
                .filter(p -> ((Number) p.get("idMatricula")).longValue() == matriculaId)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No existe un pago para la matricula " + matriculaId));

        assertEquals(metodo, pago.get("metodo"));
        numeroOperacion = (String) pago.get("numeroOperacion");
        assertNotNull(numeroOperacion, "El pago no tiene numeroOperacion");
    }

    @And("el numero de operacion tiene el formato {string}")
    public void elNumeroDeOperacionTieneElFormato(String formato) {
        assertNotNull(numeroOperacion, "No se obtuvo el numero de operacion");
        assertTrue(numeroOperacion.matches("OP-\\d{6}"),
                "El numero de operacion debe cumplir el formato " + formato);
    }
}