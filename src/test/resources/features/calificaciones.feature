Feature: Calificacion de matriculas
  Como docente de CIBERTEC
  Quiero registrar notas
  Para actualizar el historial academico del alumno

  Scenario Outline: El docente registra la nota y el historial se actualiza
    Given que el docente "D0001" inicia sesion en la API
    And que la alumna "i202610005" inicia sesion en la API
    And la alumna obtiene el id de su matricula del curso "PI5620"
    When el docente califica la matricula con la nota <nota>
    Then el codigo de respuesta es 200
    And la condicion de la matricula es "<condicion>"
    When la alumna consulta su historial
    Then el codigo de respuesta es 200
    And el historial tiene <creditosAprobados> creditos aprobados
    And el promedio ponderado es <promedioPonderado>
    And el curso "PI5620" tiene condicion "<condicion>"
    And la respuesta cumple el esquema "historial.json"

    Examples:
      | nota | condicion   | creditosAprobados | promedioPonderado |
      | 11   | DESAPROBADO | 7                 | 13.38             |
      | 12.5 | DESAPROBADO | 7                 | 14.08             |

  Scenario: Un docente que no dicta el curso intenta registrar la nota
    Given que el docente "D0002" inicia sesion en la API
    And que la alumna "i202610005" inicia sesion en la API
    And la alumna obtiene el id de su matricula del curso "PI5620"
    When el docente califica la matricula con la nota 15
    Then el codigo de respuesta es 403
    And el codigo de error es "ACCESO_DENEGADO"
    And la respuesta cumple el esquema "error.json"

  Scenario: No se puede calificar una matricula que no esta pagada
    Given que el docente "D0001" inicia sesion en la API
    And que la alumna "i202610005" inicia sesion en la API
    And la alumna obtiene el id de su matricula del curso "PS5511" en estado "REGISTRADA"
    When el docente califica la matricula con la nota 15
    Then el codigo de respuesta es 409
    And el codigo de error es "MATRICULA_NO_PAGADA"
    And la respuesta cumple el esquema "error.json"