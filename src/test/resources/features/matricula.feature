Feature: Matricula del alumno
  Como alumno de CIBERTEC
  Quiero matricularme en mis cursos
  Para llevarlos en el ciclo

  Background:
    Given que Ana consulta la API de matricula
    And inicia sesion con el usuario "i202610001" y la clave "Cibertec2026"

  Scenario: Cotizar el curso del alumno
    When cotiza el curso "PI5620"
    Then el codigo de respuesta es 200
    And la cotizacion tiene 6 creditos
    And el subtotal de la cotizacion es 720.0
    And el descuento de la cotizacion es 20
    And el total de la cotizacion es 576.0
    And la respuesta cumple el esquema "cotizacion.json"

  Scenario: Matricular y pagar el curso del alumno
    When se matricula en el curso "PI5620"
    Then el codigo de respuesta es 201
    And el estado de la matricula es "REGISTRADA"
    And el monto de la matricula es 576.0
    And la respuesta cumple el esquema "matricula.json"
    When paga la matricula con el metodo "TARJETA"
    Then el codigo de respuesta es 200
    And el estado de la matricula es "PAGADA"
    And la fecha de pago no es nula
    When consulta los pagos del alumno
    Then existe el pago de la matricula con el metodo "TARJETA"
    And el numero de operacion tiene el formato "OP-000000"

  Scenario: Regla de negocio que debe fallar
    When cotiza los cursos "PS5511,QA5517,DM5512,TS5514,SA5516,BD5410"
    Then el codigo de respuesta es 422
    And el codigo de error es "LIMITE_CREDITOS"
    And la respuesta cumple el esquema "error.json"
