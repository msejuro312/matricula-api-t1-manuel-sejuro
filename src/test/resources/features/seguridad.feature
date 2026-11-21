Feature: Seguridad de la sesion
  Como usuario de la API de matricula
  Quiero que la sesion y los permisos se validen correctamente
  Para proteger la informacion academica

  Scenario: Rotacion del refresh token
    Given que Ana consulta la API de matricula
    And inicia sesion en la API con el usuario "i202610001" y la clave "Cibertec2026"
    When renueva sus tokens
    Then el codigo de respuesta es 200
    And los tokens son nuevos
    And la respuesta cumple el esquema "token.json"
    When intenta renovar nuevamente con el refresh token original
    Then el codigo de respuesta es 401
    And el codigo de error es "REFRESH_REUTILIZADO"
    And la respuesta cumple el esquema "error.json"
    When consulta su perfil con el token actual
    Then el codigo de respuesta es 401
    And el codigo de error es "TOKEN_INVALIDO"
    And la respuesta cumple el esquema "error.json"

  Scenario: Cierre de sesion
    Given que Ana consulta la API de matricula
    And inicia sesion en la API con el usuario "i202610001" y la clave "Cibertec2026"
    When cierra su sesion
    Then el codigo de respuesta es 204
    When consulta su perfil con el token actual
    Then el codigo de respuesta es 401
    And el codigo de error es "TOKEN_INVALIDO"
    And la respuesta cumple el esquema "error.json"

  Scenario: Bloqueo de cuenta por intentos fallidos
    Given que Jorge consulta la API de matricula
    When intenta iniciar sesion con estas credenciales:
      | usuario    | clave  | status | codigo                 |
      | i202610004 | Error1 | 401    | CREDENCIALES_INVALIDAS |
      | i202610004 | Error2 | 401    | CREDENCIALES_INVALIDAS |
      | i202610004 | Error3 | 401    | CREDENCIALES_INVALIDAS |
      | i202610004 | Error4 | 401    | CREDENCIALES_INVALIDAS |
      | i202610004 | Error5 | 423    | CUENTA_BLOQUEADA       |
    Then el codigo de respuesta es 423
    And el codigo de error es "CUENTA_BLOQUEADA"
    And la respuesta cumple el esquema "error.json"
    When intenta iniciar sesion con el usuario "i202610004" y la clave "Cibertec2026"
    Then el codigo de respuesta es 423
    And el codigo de error es "CUENTA_BLOQUEADA"
    And la respuesta cumple el esquema "error.json"

  Scenario: Alumno sin permiso de administrador
    Given que Ana consulta la API de matricula
    And inicia sesion en la API con el usuario "i202610001" y la clave "Cibertec2026"
    When intenta listar todos los alumnos
    Then el codigo de respuesta es 403
    And el codigo de error es "ACCESO_DENEGADO"
    And la respuesta cumple el esquema "error.json"