Feature: Construccion de una prueba academica

  Scenario: Crear una prueba con su estructura completa

    Given existen areas de conocimiento registradas
    And existen preguntas con sus distractores asociados

    When creo una prueba con nombre "Prueba Admision 2026"

    And agrego el area "Matemáticas" a la prueba con 2 preguntas

    And agrego la pregunta "¿Cuál es el valor de la expresión 3² + 4²?" al area en la prueba
    And agrego la pregunta "¿Cuánto es el área de un triángulo con base 8 cm y altura 5 cm?" al area en la prueba

    And agrego los distractores a la primera pregunta en la prueba
    And agrego los distractores a la segunda pregunta en la prueba

    Then la prueba "Prueba Admision 2026" existe
    And la prueba tiene el area "Matemáticas"
    And el area tiene 2 preguntas
    And cada pregunta tiene 4 distractores
    And cada pregunta tiene una unica respuesta correcta