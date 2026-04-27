Feature: Crear una pregunta y asociarle un area de conocimiento y distractores

  Scenario: Crear pregunta con un area de conocimiento y distractores existentes
    Given existe un area de conocimiento llamada "Matemáticas"
    And existe un distractor con UUID d4000000-0000-0000-0000-000000000031 activo con valor "16"
    And existe un distractor con UUID d4000000-0000-0000-0000-000000000032 activo con valor "10"
    And existe un distractor con UUID d4000000-0000-0000-0000-000000000029 activo con valor "8"
    When creo una pregunta con el texto "Cuanto es 4 * 2?"
    And asocio la pregunta al area de conocimiento "Matemáticas"
    And asocio el distractor d4000000-0000-0000-0000-000000000031 a la pregunta marcando correcto como "false"
    And asocio el distractor d4000000-0000-0000-0000-000000000032 a la pregunta marcando correcto como "false"
    And asocio el distractor d4000000-0000-0000-0000-000000000029 a la pregunta marcando correcto como "true"
    Then puedo consultar la pregunta recien creada con el texto "Cuanto es 4 * 2?"
    And verifico que la pregunta pertenece al area "Matemáticas"
    And verifico que la pregunta se asocio al distractor d4000000-0000-0000-0000-000000000031
    And verifico que la pregunta se asocio al distractor d4000000-0000-0000-0000-000000000032
    And verifico que la pregunta se asocio al distractor d4000000-0000-0000-0000-000000000029