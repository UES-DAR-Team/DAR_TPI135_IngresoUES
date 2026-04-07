-- ==========================================
-- SISTEMA DE INGRESO UES
-- tablas y datos
-- ==========================================

CREATE TABLE area_conocimiento (
    id_area_conocimiento uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                   id_auto_referencia_area uuid NULL,
                                   nombre varchar(250) NOT NULL,
                                   fecha_creacion timestamp with time zone NOT NULL DEFAULT now(),
                                   activo boolean NOT NULL DEFAULT true,
                                   CONSTRAINT fk_area_autoreferencia FOREIGN KEY (id_auto_referencia_area)
                                   REFERENCES area_conocimiento (id_area_conocimiento) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE aspirante (
                           id_aspirante uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                           nombre_aspirante varchar(150) NOT NULL,
                           apellido_aspirante varchar(150) NULL,
                           identificacion varchar(20) UNIQUE NULL,
                           email varchar(255) UNIQUE NULL,
                           fecha_nacimiento date NULL,
                           fecha_registro timestamp with time zone NOT NULL DEFAULT now(),
                           activo boolean NOT NULL DEFAULT true
);

CREATE TABLE pregunta (
                          id_pregunta uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                          contenido_pregunta text NOT NULL,
                          fecha_creacion timestamp with time zone NOT NULL DEFAULT now(),
                          activo boolean NOT NULL DEFAULT true
);

CREATE TABLE distractor (
                            id_distractor uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                            contenido_distractor text NOT NULL,
                            es_correcto boolean NOT NULL DEFAULT false,
                            fecha_creacion timestamp with time zone NOT NULL DEFAULT now(),
                            activo boolean NOT NULL DEFAULT true
);

CREATE TABLE jornada (
                         id_jornada uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                         nombre_jornada varchar(150) NOT NULL,
                         fecha date NULL,
                         hora_inicio time NULL,
                         hora_fin time NULL,
                         fecha_creacion timestamp with time zone NOT NULL DEFAULT now(),
                         activo boolean NOT NULL DEFAULT true,
                         CONSTRAINT chk_jornada_horas CHECK (hora_fin > hora_inicio)
);

CREATE TABLE aula (
                      id_aula uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                      nombre_aula varchar(150) NOT NULL,
                      capacidad int NULL CHECK (capacidad > 0),
                      fecha_creacion timestamp with time zone NOT NULL DEFAULT now(),
                      activo boolean NOT NULL DEFAULT true
);

CREATE TABLE prueba (
                        id_prueba uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                        nombre_prueba varchar(250) NOT NULL,
                        fecha_creacion timestamp with time zone NOT NULL DEFAULT now(),
                        activo boolean NOT NULL DEFAULT true
);

CREATE TABLE aspirante_prueba (
                                  id_aspirante_prueba serial PRIMARY KEY,
                                  id_aspirante uuid NOT NULL,
                                  id_prueba uuid NOT NULL,
                                  fecha_asignacion timestamp with time zone NOT NULL DEFAULT now(),
                                  CONSTRAINT fk_ap_aspirante FOREIGN KEY (id_aspirante) REFERENCES aspirante (id_aspirante) ON UPDATE CASCADE ON DELETE CASCADE,
                                  CONSTRAINT fk_ap_prueba FOREIGN KEY (id_prueba) REFERENCES prueba (id_prueba) ON UPDATE CASCADE ON DELETE CASCADE,
                                  CONSTRAINT uq_ap UNIQUE (id_aspirante, id_prueba)
);

CREATE TABLE pregunta_area_conocimiento (
                                            id_pregunta_area_conocimiento serial PRIMARY KEY,
                                            id_pregunta uuid NOT NULL,
                                            id_area_conocimiento uuid NOT NULL,
                                            fecha_asignacion timestamp with time zone NOT NULL DEFAULT now(),
                                            CONSTRAINT fk_pac_pregunta FOREIGN KEY (id_pregunta) REFERENCES pregunta (id_pregunta) ON UPDATE CASCADE ON DELETE CASCADE,
                                            CONSTRAINT fk_pac_area FOREIGN KEY (id_area_conocimiento) REFERENCES area_conocimiento (id_area_conocimiento) ON UPDATE CASCADE ON DELETE CASCADE,
                                            CONSTRAINT uq_pac UNIQUE (id_pregunta, id_area_conocimiento)
);

CREATE TABLE pregunta_distractor (
                                     id_pregunta_distractor serial PRIMARY KEY,
                                     id_pregunta uuid NOT NULL,
                                     id_distractor uuid NOT NULL,
                                     orden smallint NULL,
                                     fecha_asignacion timestamp with time zone NOT NULL DEFAULT now(),
                                     CONSTRAINT fk_pd_pregunta FOREIGN KEY (id_pregunta) REFERENCES pregunta (id_pregunta) ON UPDATE CASCADE ON DELETE CASCADE,
                                     CONSTRAINT fk_pd_distractor FOREIGN KEY (id_distractor) REFERENCES distractor (id_distractor) ON UPDATE CASCADE ON DELETE CASCADE,
                                     CONSTRAINT uq_pd UNIQUE (id_pregunta, id_distractor)
);

CREATE TABLE prueba_area (
                             id_prueba_area serial PRIMARY KEY,
                             id_prueba uuid NOT NULL,
                             id_area_conocimiento uuid NOT NULL,
                             num_preguntas smallint NULL CHECK (num_preguntas > 0),
                             fecha_asignacion timestamp with time zone NOT NULL DEFAULT now(),
                             CONSTRAINT fk_paas_prueba FOREIGN KEY (id_prueba) REFERENCES prueba (id_prueba) ON UPDATE CASCADE ON DELETE CASCADE,
                             CONSTRAINT fk_paas_area FOREIGN KEY (id_area_conocimiento) REFERENCES area_conocimiento (id_area_conocimiento) ON UPDATE CASCADE ON DELETE CASCADE,
                             CONSTRAINT uq_paas UNIQUE (id_prueba, id_area_conocimiento)
);

CREATE TABLE jornada_aula (
                              id_jornada_aula serial PRIMARY KEY,
                              id_jornada uuid NOT NULL,
                              id_aula uuid NOT NULL,
                              fecha_asignacion timestamp with time zone NOT NULL DEFAULT now(),
                              CONSTRAINT fk_ja_jornada FOREIGN KEY (id_jornada) REFERENCES jornada (id_jornada) ON UPDATE CASCADE ON DELETE CASCADE,
                              CONSTRAINT fk_ja_aula FOREIGN KEY (id_aula) REFERENCES aula (id_aula) ON UPDATE CASCADE ON DELETE CASCADE,
                              CONSTRAINT uq_ja UNIQUE (id_jornada, id_aula)
);

CREATE TABLE jornada_aula_aspirante (
                                        id_jornada_aula_aspirante serial PRIMARY KEY,
                                        id_jornada_aula int NOT NULL,
                                        id_aspirante_prueba int NOT NULL,
                                        hora_llegada time NULL,
                                        asistio boolean NOT NULL DEFAULT false,
                                        fecha_asignacion timestamp with time zone NOT NULL DEFAULT now(),
                                        CONSTRAINT fk_jaa_jornada_aula FOREIGN KEY (id_jornada_aula) REFERENCES jornada_aula (id_jornada_aula) ON UPDATE CASCADE ON DELETE CASCADE,
                                        CONSTRAINT fk_jaa_aspirante_prueba FOREIGN KEY (id_aspirante_prueba) REFERENCES aspirante_prueba (id_aspirante_prueba) ON UPDATE CASCADE ON DELETE CASCADE,
                                        CONSTRAINT uq_jaa UNIQUE (id_jornada_aula, id_aspirante_prueba)
);

CREATE TABLE prueba_area_pregunta (
                                      id_prueba_area_pregunta serial PRIMARY KEY,
                                      id_prueba_area int NOT NULL,
                                      id_pregunta uuid NOT NULL,
                                      orden smallint NULL,
                                      fecha_asignacion timestamp with time zone NOT NULL DEFAULT now(),
                                      CONSTRAINT fk_paap_prueba_area FOREIGN KEY (id_prueba_area) REFERENCES prueba_area (id_prueba_area) ON UPDATE CASCADE ON DELETE CASCADE,
                                      CONSTRAINT fk_paap_pregunta FOREIGN KEY (id_pregunta) REFERENCES pregunta (id_pregunta) ON UPDATE CASCADE ON DELETE CASCADE,
                                      CONSTRAINT uq_paap UNIQUE (id_prueba_area, id_pregunta)
);

CREATE TABLE prueba_area_pregunta_distractor (
                                                 id_prueba_area_pregunta_distractor serial PRIMARY KEY,
                                                 id_prueba_area_pregunta int NOT NULL,
                                                 id_distractor uuid NOT NULL,
                                                 es_respuesta_correcta boolean NULL,
                                                 fecha_registro timestamp with time zone NOT NULL DEFAULT now(),
                                                 CONSTRAINT fk_paapd_prueba_area_pregunta FOREIGN KEY (id_prueba_area_pregunta) REFERENCES prueba_area_pregunta (id_prueba_area_pregunta) ON UPDATE CASCADE ON DELETE CASCADE,
                                                 CONSTRAINT fk_paapd_distractor FOREIGN KEY (id_distractor) REFERENCES distractor (id_distractor) ON UPDATE CASCADE ON DELETE CASCADE,
                                                 CONSTRAINT uq_paapd UNIQUE (id_prueba_area_pregunta, id_distractor)
);

CREATE TABLE jornada_aula_aspirante_resultado (
                                                  id_jornada_aula_aspirante_resultado serial PRIMARY KEY,
                                                  id_jornada_aula_aspirante int NOT NULL,
                                                  puntaje_obtenido numeric(6,2) NULL,
                                                  aprobado boolean NULL,
                                                  fecha_calificacion timestamp with time zone NULL,
                                                  CONSTRAINT fk_jaar_jornada_aula_aspirante FOREIGN KEY (id_jornada_aula_aspirante) REFERENCES jornada_aula_aspirante (id_jornada_aula_aspirante) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE aspirante_opciones (
                                    id_aspirante_opciones serial PRIMARY KEY,
                                    id_aspirante uuid NOT NULL,
                                    codigo_programa varchar(20) NULL,
                                    nombre_programa varchar(250) NULL,
                                    fecha_seleccion timestamp with time zone NOT NULL DEFAULT now(),
                                    CONSTRAINT fk_ao_aspirante FOREIGN KEY (id_aspirante) REFERENCES aspirante (id_aspirante) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE distractor_area_conocimiento (
                                              id_distractor_area_conocimiento serial PRIMARY KEY,
                                              id_distractor uuid NOT NULL,
                                              id_area_conocimiento uuid NOT NULL,
                                              fecha_asignacion timestamp with time zone NOT NULL DEFAULT now(),
                                              CONSTRAINT fk_dac_distractor FOREIGN KEY (id_distractor) REFERENCES distractor (id_distractor) ON UPDATE CASCADE ON DELETE CASCADE,
                                              CONSTRAINT fk_dac_area FOREIGN KEY (id_area_conocimiento) REFERENCES area_conocimiento (id_area_conocimiento) ON UPDATE CASCADE ON DELETE CASCADE,
                                              CONSTRAINT uq_dac UNIQUE (id_distractor, id_area_conocimiento)
);

CREATE TABLE prueba_jornada (
                                id_prueba_jornada serial PRIMARY KEY,
                                id_prueba uuid NOT NULL,
                                id_jornada uuid NOT NULL,
                                fecha_asignacion timestamp with time zone NOT NULL DEFAULT now(),
                                CONSTRAINT fk_paj_prueba FOREIGN KEY (id_prueba) REFERENCES prueba (id_prueba) ON UPDATE CASCADE ON DELETE CASCADE,
                                CONSTRAINT fk_pj_jornada FOREIGN KEY (id_jornada) REFERENCES jornada (id_jornada) ON UPDATE CASCADE ON DELETE CASCADE,
                                CONSTRAINT uq_paj UNIQUE (id_prueba, id_jornada)
);


-- ==========================================
-- INSERCIÓN DE DATOS
-- ==========================================

-- 1. ÁREAS DE CONOCIMIENTO
INSERT INTO area_conocimiento (id_area_conocimiento, id_auto_referencia_area, nombre) VALUES
                                                                                          ('a1000000-0000-0000-0000-000000000001', NULL,                                   'Matemáticas'),
                                                                                          ('a1000000-0000-0000-0000-000000000002', NULL,                                   'Lenguaje y Literatura'),
                                                                                          ('a1000000-0000-0000-0000-000000000003', NULL,                                   'Ciencias Naturales'),
                                                                                          ('a1000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000003', 'Biología'),
                                                                                          ('a1000000-0000-0000-0000-000000000005', 'a1000000-0000-0000-0000-000000000003', 'Química');

-- 2. ASPIRANTES
INSERT INTO aspirante (id_aspirante, nombre_aspirante, apellido_aspirante, identificacion, email, fecha_nacimiento) VALUES
                                                                                                                        ('b2000000-0000-0000-0000-000000000001', 'Carlos Ernesto', 'Martínez López',  '05010101-1', 'carlos.martinez@gmail.com', '2005-03-14'),
                                                                                                                        ('b2000000-0000-0000-0000-000000000002', 'María Fernanda', 'González Rivas',  '01020202-2', 'maria.gonzalez@gmail.com',  '2005-07-22'),
                                                                                                                        ('b2000000-0000-0000-0000-000000000003', 'José Alejandro', 'Hernández Cruz',  '02030303-3', 'jose.hernandez@gmail.com',  '2004-11-05'),
                                                                                                                        ('b2000000-0000-0000-0000-000000000004', 'Ana Sofía',      'Pérez Molina',    '03040404-4', 'ana.perez@gmail.com',       '2005-01-30'),
                                                                                                                        ('b2000000-0000-0000-0000-000000000005', 'Luis Enrique',   'Ramírez Vásquez', '04050505-5', 'luis.ramirez@gmail.com',    '2004-09-18');

-- 3. PREGUNTAS
INSERT INTO pregunta (id_pregunta, contenido_pregunta) VALUES
                                                           ('c3000000-0000-0000-0000-000000000001', '¿Cuál es el resultado de resolver la ecuación 2x + 5 = 13?'),
                                                           ('c3000000-0000-0000-0000-000000000002', '¿Cuánto es el área de un triángulo con base 8 cm y altura 5 cm?'),
                                                           ('c3000000-0000-0000-0000-000000000003', '¿Cuál es el valor de la expresión 3² + 4²?'),
                                                           ('c3000000-0000-0000-0000-000000000004', '¿Qué figura literaria se utiliza cuando se atribuyen cualidades humanas a objetos inanimados?'),
                                                           ('c3000000-0000-0000-0000-000000000005', '¿Cuál de las siguientes oraciones está escrita correctamente con tilde?'),
                                                           ('c3000000-0000-0000-0000-000000000006', '¿Cuál es la función principal de las mitocondrias en la célula?'),
                                                           ('c3000000-0000-0000-0000-000000000007', '¿Qué tipo de reproducción no requiere la unión de gametos?'),
                                                           ('c3000000-0000-0000-0000-000000000008', '¿Cuántos electrones tiene un átomo de oxígeno en su estado neutro?'),
                                                           ('c3000000-0000-0000-0000-000000000009', '¿Qué tipo de enlace se forma entre dos átomos de hidrógeno?');

-- 4. DISTRACTORES
INSERT INTO distractor (id_distractor, contenido_distractor, es_correcto) VALUES
                                                                              ('d4000000-0000-0000-0000-000000000001', 'x = 4',  true),
                                                                              ('d4000000-0000-0000-0000-000000000002', 'x = 3',  false),
                                                                              ('d4000000-0000-0000-0000-000000000003', 'x = 9',  false),
                                                                              ('d4000000-0000-0000-0000-000000000004', 'x = 6',  false),
                                                                              ('d4000000-0000-0000-0000-000000000005', '20 cm²', true),
                                                                              ('d4000000-0000-0000-0000-000000000006', '40 cm²', false),
                                                                              ('d4000000-0000-0000-0000-000000000007', '13 cm²', false),
                                                                              ('d4000000-0000-0000-0000-000000000008', '16 cm²', false),
                                                                              ('d4000000-0000-0000-0000-000000000009', '25',     true),
                                                                              ('d4000000-0000-0000-0000-000000000010', '14',     false),
                                                                              ('d4000000-0000-0000-0000-000000000011', '49',     false),
                                                                              ('d4000000-0000-0000-0000-000000000012', '7',      false),
                                                                              ('d4000000-0000-0000-0000-000000000013', 'Personificación', true),
                                                                              ('d4000000-0000-0000-0000-000000000014', 'Metáfora',        false),
                                                                              ('d4000000-0000-0000-0000-000000000015', 'Hipérbole',       false),
                                                                              ('d4000000-0000-0000-0000-000000000016', 'Símil',           false),
                                                                              ('d4000000-0000-0000-0000-000000000017', 'El árbol creció rápido.',  true),
                                                                              ('d4000000-0000-0000-0000-000000000018', 'El arbol crecio rapido.',  false),
                                                                              ('d4000000-0000-0000-0000-000000000019', 'El árbol crecio rápido.',  false),
                                                                              ('d4000000-0000-0000-0000-000000000020', 'El arbol creció rapido.',  false),
                                                                              ('d4000000-0000-0000-0000-000000000021', 'Producir energía en forma de ATP',  true),
                                                                              ('d4000000-0000-0000-0000-000000000022', 'Sintetizar proteínas',              false),
                                                                              ('d4000000-0000-0000-0000-000000000023', 'Almacenar el material genético',    false),
                                                                              ('d4000000-0000-0000-0000-000000000024', 'Regular el transporte celular',     false),
                                                                              ('d4000000-0000-0000-0000-000000000025', 'Reproducción asexual',  true),
                                                                              ('d4000000-0000-0000-0000-000000000026', 'Reproducción sexual',   false),
                                                                              ('d4000000-0000-0000-0000-000000000027', 'Fecundación interna',   false),
                                                                              ('d4000000-0000-0000-0000-000000000028', 'Meiosis',               false),
                                                                              ('d4000000-0000-0000-0000-000000000029', '8',   true),
                                                                              ('d4000000-0000-0000-0000-000000000030', '6',   false),
                                                                              ('d4000000-0000-0000-0000-000000000031', '16',  false),
                                                                              ('d4000000-0000-0000-0000-000000000032', '10',  false),
                                                                              ('d4000000-0000-0000-0000-000000000033', 'Enlace covalente',    true),
                                                                              ('d4000000-0000-0000-0000-000000000034', 'Enlace iónico',       false),
                                                                              ('d4000000-0000-0000-0000-000000000035', 'Enlace metálico',     false),
                                                                              ('d4000000-0000-0000-0000-000000000036', 'Enlace de hidrógeno', false);

-- 5. JORNADAS
INSERT INTO jornada (id_jornada, nombre_jornada, fecha, hora_inicio, hora_fin) VALUES
                                                                                   ('e5000000-0000-0000-0000-000000000001', 'Jornada Matutina Abril 2025',   '2025-04-12', '07:00', '11:00'),
                                                                                   ('e5000000-0000-0000-0000-000000000002', 'Jornada Vespertina Abril 2025', '2025-04-12', '13:00', '17:00'),
                                                                                   ('e5000000-0000-0000-0000-000000000003', 'Jornada Matutina Mayo 2025',    '2025-05-10', '07:00', '11:00');

-- 6. AULAS
INSERT INTO aula (id_aula, nombre_aula, capacidad) VALUES
                                                       ('f6000000-0000-0000-0000-000000000001', 'Aula 101 - Edificio A',  30),
                                                       ('f6000000-0000-0000-0000-000000000002', 'Aula 202 - Edificio B',  25),
                                                       ('f6000000-0000-0000-0000-000000000003', 'Laboratorio de Cómputo', 20);

-- 7. PRUEBAS
INSERT INTO prueba (id_prueba, nombre_prueba) VALUES
                                                  ('07000000-0000-0000-0000-000000000001', 'Prueba General de Admisión 2025'),
                                                  ('07000000-0000-0000-0000-000000000002', 'Prueba de Ciencias Naturales 2025'),
                                                  ('07000000-0000-0000-0000-000000000003', 'Prueba de Humanidades 2025');

-- 8. ASPIRANTE ↔ PRUEBA
INSERT INTO aspirante_prueba (id_aspirante, id_prueba) VALUES
                                                           ('b2000000-0000-0000-0000-000000000001', '07000000-0000-0000-0000-000000000001'),
                                                           ('b2000000-0000-0000-0000-000000000002', '07000000-0000-0000-0000-000000000001'),
                                                           ('b2000000-0000-0000-0000-000000000003', '07000000-0000-0000-0000-000000000002'),
                                                           ('b2000000-0000-0000-0000-000000000004', '07000000-0000-0000-0000-000000000002'),
                                                           ('b2000000-0000-0000-0000-000000000005', '07000000-0000-0000-0000-000000000003');

-- 9. PREGUNTA ↔ ÁREA
INSERT INTO pregunta_area_conocimiento (id_pregunta, id_area_conocimiento) VALUES
                                                                               ('c3000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000001'),
                                                                               ('c3000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000001'),
                                                                               ('c3000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000001'),
                                                                               ('c3000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000002'),
                                                                               ('c3000000-0000-0000-0000-000000000005', 'a1000000-0000-0000-0000-000000000002'),
                                                                               ('c3000000-0000-0000-0000-000000000006', 'a1000000-0000-0000-0000-000000000004'),
                                                                               ('c3000000-0000-0000-0000-000000000007', 'a1000000-0000-0000-0000-000000000004'),
                                                                               ('c3000000-0000-0000-0000-000000000008', 'a1000000-0000-0000-0000-000000000005'),
                                                                               ('c3000000-0000-0000-0000-000000000009', 'a1000000-0000-0000-0000-000000000005');

-- 10. PREGUNTA ↔ DISTRACTOR
INSERT INTO pregunta_distractor (id_pregunta, id_distractor, orden) VALUES
                                                                        ('c3000000-0000-0000-0000-000000000001', 'd4000000-0000-0000-0000-000000000001', 1),
                                                                        ('c3000000-0000-0000-0000-000000000001', 'd4000000-0000-0000-0000-000000000002', 2),
                                                                        ('c3000000-0000-0000-0000-000000000001', 'd4000000-0000-0000-0000-000000000003', 3),
                                                                        ('c3000000-0000-0000-0000-000000000001', 'd4000000-0000-0000-0000-000000000004', 4),
                                                                        ('c3000000-0000-0000-0000-000000000002', 'd4000000-0000-0000-0000-000000000005', 1),
                                                                        ('c3000000-0000-0000-0000-000000000002', 'd4000000-0000-0000-0000-000000000006', 2),
                                                                        ('c3000000-0000-0000-0000-000000000002', 'd4000000-0000-0000-0000-000000000007', 3),
                                                                        ('c3000000-0000-0000-0000-000000000002', 'd4000000-0000-0000-0000-000000000008', 4),
                                                                        ('c3000000-0000-0000-0000-000000000003', 'd4000000-0000-0000-0000-000000000009', 1),
                                                                        ('c3000000-0000-0000-0000-000000000003', 'd4000000-0000-0000-0000-000000000010', 2),
                                                                        ('c3000000-0000-0000-0000-000000000003', 'd4000000-0000-0000-0000-000000000011', 3),
                                                                        ('c3000000-0000-0000-0000-000000000003', 'd4000000-0000-0000-0000-000000000012', 4),
                                                                        ('c3000000-0000-0000-0000-000000000004', 'd4000000-0000-0000-0000-000000000013', 1),
                                                                        ('c3000000-0000-0000-0000-000000000004', 'd4000000-0000-0000-0000-000000000014', 2),
                                                                        ('c3000000-0000-0000-0000-000000000004', 'd4000000-0000-0000-0000-000000000015', 3),
                                                                        ('c3000000-0000-0000-0000-000000000004', 'd4000000-0000-0000-0000-000000000016', 4),
                                                                        ('c3000000-0000-0000-0000-000000000005', 'd4000000-0000-0000-0000-000000000017', 1),
                                                                        ('c3000000-0000-0000-0000-000000000005', 'd4000000-0000-0000-0000-000000000018', 2),
                                                                        ('c3000000-0000-0000-0000-000000000005', 'd4000000-0000-0000-0000-000000000019', 3),
                                                                        ('c3000000-0000-0000-0000-000000000005', 'd4000000-0000-0000-0000-000000000020', 4),
                                                                        ('c3000000-0000-0000-0000-000000000006', 'd4000000-0000-0000-0000-000000000021', 1),
                                                                        ('c3000000-0000-0000-0000-000000000006', 'd4000000-0000-0000-0000-000000000022', 2),
                                                                        ('c3000000-0000-0000-0000-000000000006', 'd4000000-0000-0000-0000-000000000023', 3),
                                                                        ('c3000000-0000-0000-0000-000000000006', 'd4000000-0000-0000-0000-000000000024', 4),
                                                                        ('c3000000-0000-0000-0000-000000000007', 'd4000000-0000-0000-0000-000000000025', 1),
                                                                        ('c3000000-0000-0000-0000-000000000007', 'd4000000-0000-0000-0000-000000000026', 2),
                                                                        ('c3000000-0000-0000-0000-000000000007', 'd4000000-0000-0000-0000-000000000027', 3),
                                                                        ('c3000000-0000-0000-0000-000000000007', 'd4000000-0000-0000-0000-000000000028', 4),
                                                                        ('c3000000-0000-0000-0000-000000000008', 'd4000000-0000-0000-0000-000000000029', 1),
                                                                        ('c3000000-0000-0000-0000-000000000008', 'd4000000-0000-0000-0000-000000000030', 2),
                                                                        ('c3000000-0000-0000-0000-000000000008', 'd4000000-0000-0000-0000-000000000031', 3),
                                                                        ('c3000000-0000-0000-0000-000000000008', 'd4000000-0000-0000-0000-000000000032', 4),
                                                                        ('c3000000-0000-0000-0000-000000000009', 'd4000000-0000-0000-0000-000000000033', 1),
                                                                        ('c3000000-0000-0000-0000-000000000009', 'd4000000-0000-0000-0000-000000000034', 2),
                                                                        ('c3000000-0000-0000-0000-000000000009', 'd4000000-0000-0000-0000-000000000035', 3),
                                                                        ('c3000000-0000-0000-0000-000000000009', 'd4000000-0000-0000-0000-000000000036', 4);

-- 11. PRUEBA  ÁREA
INSERT INTO prueba_area (id_prueba, id_area_conocimiento, num_preguntas) VALUES
                                                                             ('07000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000001', 3),
                                                                             ('07000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000002', 2),
                                                                             ('07000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000004', 2),
                                                                             ('07000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000005', 2),
                                                                             ('07000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000002', 2),
                                                                             ('07000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000001', 1);

-- 12. JORNADA  AULA
INSERT INTO jornada_aula (id_jornada, id_aula) VALUES
                                                   ('e5000000-0000-0000-0000-000000000001', 'f6000000-0000-0000-0000-000000000001'),
                                                   ('e5000000-0000-0000-0000-000000000001', 'f6000000-0000-0000-0000-000000000002'),
                                                   ('e5000000-0000-0000-0000-000000000002', 'f6000000-0000-0000-0000-000000000003'),
                                                   ('e5000000-0000-0000-0000-000000000003', 'f6000000-0000-0000-0000-000000000001');

-- 13. PRUEBA JORNADA
INSERT INTO prueba_jornada (id_prueba, id_jornada) VALUES
                                                       ('07000000-0000-0000-0000-000000000001', 'e5000000-0000-0000-0000-000000000001'),
                                                       ('07000000-0000-0000-0000-000000000002', 'e5000000-0000-0000-0000-000000000002'),
                                                       ('07000000-0000-0000-0000-000000000003', 'e5000000-0000-0000-0000-000000000003');

-- 14. PRUEBA_ÁREA PREGUNTA
INSERT INTO prueba_area_pregunta (id_prueba_area, id_pregunta, orden) VALUES
                                                                          (1, 'c3000000-0000-0000-0000-000000000001', 1),
                                                                          (1, 'c3000000-0000-0000-0000-000000000002', 2),
                                                                          (1, 'c3000000-0000-0000-0000-000000000003', 3),
                                                                          (2, 'c3000000-0000-0000-0000-000000000004', 1),
                                                                          (2, 'c3000000-0000-0000-0000-000000000005', 2),
                                                                          (3, 'c3000000-0000-0000-0000-000000000006', 1),
                                                                          (3, 'c3000000-0000-0000-0000-000000000007', 2),
                                                                          (4, 'c3000000-0000-0000-0000-000000000008', 1),
                                                                          (4, 'c3000000-0000-0000-0000-000000000009', 2),
                                                                          (5, 'c3000000-0000-0000-0000-000000000004', 1),
                                                                          (5, 'c3000000-0000-0000-0000-000000000005', 2),
                                                                          (6, 'c3000000-0000-0000-0000-000000000001', 1);

-- 15. PRUEBA_ÁREA_PREGUNTA DISTRACTOR
INSERT INTO prueba_area_pregunta_distractor (id_prueba_area_pregunta, id_distractor, es_respuesta_correcta) VALUES
                                                                                                                (1,  'd4000000-0000-0000-0000-000000000001', true),
                                                                                                                (1,  'd4000000-0000-0000-0000-000000000002', false),
                                                                                                                (1,  'd4000000-0000-0000-0000-000000000003', false),
                                                                                                                (1,  'd4000000-0000-0000-0000-000000000004', false),
                                                                                                                (2,  'd4000000-0000-0000-0000-000000000005', true),
                                                                                                                (2,  'd4000000-0000-0000-0000-000000000006', false),
                                                                                                                (2,  'd4000000-0000-0000-0000-000000000007', false),
                                                                                                                (2,  'd4000000-0000-0000-0000-000000000008', false),
                                                                                                                (3,  'd4000000-0000-0000-0000-000000000009', true),
                                                                                                                (3,  'd4000000-0000-0000-0000-000000000010', false),
                                                                                                                (3,  'd4000000-0000-0000-0000-000000000011', false),
                                                                                                                (3,  'd4000000-0000-0000-0000-000000000012', false),
                                                                                                                (4,  'd4000000-0000-0000-0000-000000000013', true),
                                                                                                                (4,  'd4000000-0000-0000-0000-000000000014', false),
                                                                                                                (4,  'd4000000-0000-0000-0000-000000000015', false),
                                                                                                                (4,  'd4000000-0000-0000-0000-000000000016', false),
                                                                                                                (5,  'd4000000-0000-0000-0000-000000000017', true),
                                                                                                                (5,  'd4000000-0000-0000-0000-000000000018', false),
                                                                                                                (5,  'd4000000-0000-0000-0000-000000000019', false),
                                                                                                                (5,  'd4000000-0000-0000-0000-000000000020', false),
                                                                                                                (6,  'd4000000-0000-0000-0000-000000000021', true),
                                                                                                                (6,  'd4000000-0000-0000-0000-000000000022', false),
                                                                                                                (6,  'd4000000-0000-0000-0000-000000000023', false),
                                                                                                                (6,  'd4000000-0000-0000-0000-000000000024', false),
                                                                                                                (7,  'd4000000-0000-0000-0000-000000000025', true),
                                                                                                                (7,  'd4000000-0000-0000-0000-000000000026', false),
                                                                                                                (7,  'd4000000-0000-0000-0000-000000000027', false),
                                                                                                                (7,  'd4000000-0000-0000-0000-000000000028', false),
                                                                                                                (8,  'd4000000-0000-0000-0000-000000000029', true),
                                                                                                                (8,  'd4000000-0000-0000-0000-000000000030', false),
                                                                                                                (8,  'd4000000-0000-0000-0000-000000000031', false),
                                                                                                                (8,  'd4000000-0000-0000-0000-000000000032', false),
                                                                                                                (9,  'd4000000-0000-0000-0000-000000000033', true),
                                                                                                                (9,  'd4000000-0000-0000-0000-000000000034', false),
                                                                                                                (9,  'd4000000-0000-0000-0000-000000000035', false),
                                                                                                                (9,  'd4000000-0000-0000-0000-000000000036', false),
                                                                                                                (10, 'd4000000-0000-0000-0000-000000000013', true),
                                                                                                                (10, 'd4000000-0000-0000-0000-000000000014', false),
                                                                                                                (10, 'd4000000-0000-0000-0000-000000000015', false),
                                                                                                                (10, 'd4000000-0000-0000-0000-000000000016', false),
                                                                                                                (11, 'd4000000-0000-0000-0000-000000000017', true),
                                                                                                                (11, 'd4000000-0000-0000-0000-000000000018', false),
                                                                                                                (11, 'd4000000-0000-0000-0000-000000000019', false),
                                                                                                                (11, 'd4000000-0000-0000-0000-000000000020', false),
                                                                                                                (12, 'd4000000-0000-0000-0000-000000000001', true),
                                                                                                                (12, 'd4000000-0000-0000-0000-000000000002', false),
                                                                                                                (12, 'd4000000-0000-0000-0000-000000000003', false),
                                                                                                                (12, 'd4000000-0000-0000-0000-000000000004', false);

-- 16. JORNADA_AULA  ASPIRANTE_PRUEBA
INSERT INTO jornada_aula_aspirante (id_jornada_aula, id_aspirante_prueba, hora_llegada, asistio) VALUES
                                                                                                     (1, 1, '06:50', true),
                                                                                                     (1, 2, '07:05', true),
                                                                                                     (3, 3, '12:55', true),
                                                                                                     (3, 4, '13:10', true),
                                                                                                     (4, 5, '06:45', true);

-- 17. RESULTADOS
INSERT INTO jornada_aula_aspirante_resultado (id_jornada_aula_aspirante, puntaje_obtenido, aprobado, fecha_calificacion) VALUES
                                                                                                                             (1, 8.50, true,  '2025-04-12 12:00:00-06'),
                                                                                                                             (2, 6.00, true,  '2025-04-12 12:00:00-06'),
                                                                                                                             (3, 9.00, true,  '2025-04-12 18:00:00-06'),
                                                                                                                             (4, 4.50, false, '2025-04-12 18:00:00-06'),
                                                                                                                             (5, 7.75, true,  '2025-05-10 12:00:00-06');

-- 18. DISTRACTOR ↔ ÁREA CONOCIMIENTO
INSERT INTO distractor_area_conocimiento (id_distractor, id_area_conocimiento) VALUES
                                                                                   ('d4000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000005', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000006', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000007', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000008', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000009', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000010', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000011', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000012', 'a1000000-0000-0000-0000-000000000001'),
                                                                                   ('d4000000-0000-0000-0000-000000000013', 'a1000000-0000-0000-0000-000000000002'),
                                                                                   ('d4000000-0000-0000-0000-000000000014', 'a1000000-0000-0000-0000-000000000002'),
                                                                                   ('d4000000-0000-0000-0000-000000000015', 'a1000000-0000-0000-0000-000000000002'),
                                                                                   ('d4000000-0000-0000-0000-000000000016', 'a1000000-0000-0000-0000-000000000002'),
                                                                                   ('d4000000-0000-0000-0000-000000000017', 'a1000000-0000-0000-0000-000000000002'),
                                                                                   ('d4000000-0000-0000-0000-000000000018', 'a1000000-0000-0000-0000-000000000002'),
                                                                                   ('d4000000-0000-0000-0000-000000000019', 'a1000000-0000-0000-0000-000000000002'),
                                                                                   ('d4000000-0000-0000-0000-000000000020', 'a1000000-0000-0000-0000-000000000002'),
                                                                                   ('d4000000-0000-0000-0000-000000000021', 'a1000000-0000-0000-0000-000000000004'),
                                                                                   ('d4000000-0000-0000-0000-000000000022', 'a1000000-0000-0000-0000-000000000004'),
                                                                                   ('d4000000-0000-0000-0000-000000000023', 'a1000000-0000-0000-0000-000000000004'),
                                                                                   ('d4000000-0000-0000-0000-000000000024', 'a1000000-0000-0000-0000-000000000004'),
                                                                                   ('d4000000-0000-0000-0000-000000000025', 'a1000000-0000-0000-0000-000000000004'),
                                                                                   ('d4000000-0000-0000-0000-000000000026', 'a1000000-0000-0000-0000-000000000004'),
                                                                                   ('d4000000-0000-0000-0000-000000000027', 'a1000000-0000-0000-0000-000000000004'),
                                                                                   ('d4000000-0000-0000-0000-000000000028', 'a1000000-0000-0000-0000-000000000004'),
                                                                                   ('d4000000-0000-0000-0000-000000000029', 'a1000000-0000-0000-0000-000000000005'),
                                                                                   ('d4000000-0000-0000-0000-000000000030', 'a1000000-0000-0000-0000-000000000005'),
                                                                                   ('d4000000-0000-0000-0000-000000000031', 'a1000000-0000-0000-0000-000000000005'),
                                                                                   ('d4000000-0000-0000-0000-000000000032', 'a1000000-0000-0000-0000-000000000005'),
                                                                                   ('d4000000-0000-0000-0000-000000000033', 'a1000000-0000-0000-0000-000000000005'),
                                                                                   ('d4000000-0000-0000-0000-000000000034', 'a1000000-0000-0000-0000-000000000005'),
                                                                                   ('d4000000-0000-0000-0000-000000000035', 'a1000000-0000-0000-0000-000000000005'),
                                                                                   ('d4000000-0000-0000-0000-000000000036', 'a1000000-0000-0000-0000-000000000005');

-- 19. OPCIONES DE CARRERA
INSERT INTO aspirante_opciones (id_aspirante, codigo_programa, nombre_programa) VALUES
                                                                                    ('b2000000-0000-0000-0000-000000000001', 'ING-SIS', 'Ingeniería en Sistemas Informáticos'),
                                                                                    ('b2000000-0000-0000-0000-000000000001', 'ING-IND', 'Ingeniería Industrial'),
                                                                                    ('b2000000-0000-0000-0000-000000000002', 'MED',     'Licenciatura en Medicina'),
                                                                                    ('b2000000-0000-0000-0000-000000000003', 'BIO',     'Licenciatura en Biología'),
                                                                                    ('b2000000-0000-0000-0000-000000000004', 'QUI-FAR', 'Licenciatura en Química y Farmacia'),
                                                                                    ('b2000000-0000-0000-0000-000000000005', 'DER',     'Licenciatura en Ciencias Jurídicas');