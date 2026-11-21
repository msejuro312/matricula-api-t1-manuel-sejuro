Feature: Consulta de cursos
  Como alumno de CIBERTEC
  Quiero consultar los cursos disponibles
  Para decidir en cuales me matriculo


  Scenario: Consultar un cursos por codigo
    Given que Ana consulta la API de matricula
    When busca el curso "PS5511"
    Then el codigo de respuesta es 200
    And el curso se llama "Pruebas de Software"
    And la respuesta cumple el esquema "curso.json"

  Scenario: Consultar un curso que no existe
    Given que Ana consulta la API de matricula
    When busca el curso "XX9999"
    Then el codigo de respuesta es 404
    And el codigo de error es "CURSO_NO_ENCONTRADO"

  Scenario Outline: Filtrar cursos por modalidad
    Given que Ana consulta la API de matricula
    When lista los cursos con modalidad "<modalidad>"
    Then el codigo de respuesta es 200
    And obtiene <cantidad> cursos
    Examples:
      | modalidad  | cantidad |
      | PRESENCIAL | 5        |
      | VIRTUAL    | 4        |
      | HIBRIDO    | 2        |