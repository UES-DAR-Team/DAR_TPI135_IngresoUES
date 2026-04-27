Feature: Crear una pregunta y asociarle un area de conocimiento y distractores

  Scenario: Crear pregunta con un area de conocimiento y distractores existentes
    Given existe un area de conocimiento llamada "Matemáticas"
    And existe un primer distractor activo con valor "16"
    And existe un segundo distractor activo con valor "8"
    When creo una pregunta con el texto "Cuanto es 4 * 2?"
    And asocio la pregunta al area de conocimiento "Matemáticas"
    And asocio el primer distractor a la pregunta marcando esCorrecto como "false"
    And asocio el segundo distractor a la pregunta marcando esCorrecto como "true"
    Then puedo consultar la pregunta recien creada con el texto "Cuanto es 4 * 2?"
    And verifico que la pregunta pertenece al area "Matemáticas"
    And verifico que la pregunta se asocio al primer distractor
    And verifico que la pregunta se asocio al segundo distractor