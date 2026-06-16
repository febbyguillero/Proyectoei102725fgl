--
-- SgOVI - Datos de ejemplo (PostgreSQL)
-- Grupo ei102725fgl
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- usuariovi
--
INSERT INTO public.usuariovi
    (id_usuari, identificador_sgovi, contrasenya, email, nom, cognoms, telefon, adreca, dni, data_naixement, consentiment_informat, data_registre, estat_tecnic_acceptat, tutor_legal_nom, tutor_legal_contacte, zona_geografica)
VALUES
(1, 'USR001', 'pass1234', 'marta.lopez@email.com', 'Marta', 'López García', '654321987', 'C/ Mayor 23, Castelló', '12345678A', '1985-03-15', true, '2026-02-21 10:30:00', true,  NULL, NULL, 'Castelló Centre'),
(2, 'USR002', 'pass5678', 'joan.martinez@email.com', 'Joan', 'Martínez Ruiz', '678543219', 'Av/ de la Plana 45, Castelló', '87654321B', '1990-07-22', true, '2026-02-21 10:30:00', true,  NULL, NULL, 'Castelló Sud'),
(3, 'USR003', 'pass9012', 'carla.sanchez@email.com', 'Carla', 'Sánchez Pérez', '612345678', 'C/ Colom 12, Castelló', '11223344C', '1978-11-30', true, '2026-02-21 10:30:00', false, NULL, NULL, 'Castelló Nord'),
(4, 'USR004', 'pass3456', 'pere.ramirez@email.com', 'Pere', 'Ramírez Gómez', '699887766', 'Plça Major 3, Castelló', '22334455D', '1982-09-10', true, '2026-02-21 10:30:00', true,  NULL, NULL, 'Grau de Castelló'),
(5, 'USR005', 'pass7890', 'anna.ferrer@email.com', 'Anna', 'Ferrer Navarro', '677889900', 'C/ La Pau 67, Castelló', '33445566E', '1995-05-18', true, '2026-02-21 10:30:00', false, NULL, NULL, 'Castelló Centre');

--
-- asistentepersonal
--
INSERT INTO public.asistentepersonal
    (dni, nombre, apellidos, email, telefono, edad, titulacion, estado, zona_geografica, disponibilidad, formacion, experiencia)
VALUES
('11111111A', 'María',  'García López',      'maria.garcia@email.com',     '654123789', 28, 'Grado en Enfermería',    'ACEPTADO',  'Castelló Centre', 'Matins i vesprades',     'Curs d''asistència personal OVI', '3 anys com a PAP'),
('22222222B', 'Juan',   'Martínez Sánchez',  'juan.martinez@email.com',    '654987321', 35, 'Psicología',             'ACEPTADO',  'Castelló Sud',    'Matins',                  'Formació en dependència',         '5 anys en atenció a persones'),
('33333333C', 'Laura',  'Rodríguez Pérez',   'laura.rodriguez@email.com',  '655123456', 24, 'Educación Social',       'PENDIENTE', 'Castelló Nord',   'Vesprades',               'Grau en Educació Social',         'Pràctiques en residència'),
('44444444D', 'Carlos', 'Ferrandiz Ruiz',    'carlos.ferrandiz@email.com', '656789123', 42, 'Trabajo Social',         'ACEPTADO',  'Castelló Centre', 'Jornada completa',        'Curs PATI',                       '10 anys en serveis socials'),
('55555555E', 'Ana',    'Montero Beltrán',   'ana.montero@email.com',      '657234567', 31, 'Terapia Ocupacional',    'ACEPTADO',  'Grau de Castelló','Matins i caps de setmana','Màster en teràpia ocupacional',   '4 anys com a PATI');

--
-- aprequest (solicitudes). usuari_ident = id numérico del usuario que la creó.
--
INSERT INTO public.aprequest
    (id_request, usuari_ident, tipus_servei, estat, data_creacio, observations, dies, franja_horaria)
VALUES
(1, '1', 'PAP',  'PENDIENTE', '2026-01-15', 'Solicitud inicial para asistente personal',       'L,M,X',     'mañana'),
(2, '2', 'PATI', 'APROBADA',  '2026-01-18', 'Documentación correcta, asignación realizada',     'L,J',       'tarde'),
(3, '3', 'PAP',  'RECHAZADA', '2026-01-20', 'No cumple los requisitos',                          'V',         'mañana'),
(4, '4', 'PATI', 'PENDIENTE', '2026-01-22', 'Pendiente de validación por técnico',               'M,X,J',     'tarde'),
(5, '5', 'PAP',  'APROBADA',  '2026-01-25', 'Aprobada, en proceso de selección',                 'L,M,X,J,V', 'mañana');

--
-- actividad_formacion
--
INSERT INTO public.actividad_formacion VALUES
(1, 'Curso de Asistencia Personal', 'Formación básica para asistentes personales', 'FORMACION', '2026-03-01 09:00:00', '2026-03-05 14:00:00', 'Centro OVI Castelló', 30, 'programada'),
(2, 'Charla Vida Independiente', 'Divulgación sobre vida autónoma', 'DIVULGACION', '2026-02-28 18:00:00', '2026-02-28 20:00:00', 'Salón de Actos UJI', NULL, 'programada'),
(3, 'Taller de Autogestión', 'Taller práctico para usuarios', 'FORMACION', '2026-03-10 10:00:00', '2026-03-12 13:00:00', 'Sede OVI', 25, 'programada'),
(4, 'Jornada de Sensibilización', 'Actividad abierta al público', 'DIVULGACION', '2026-03-15 17:00:00', '2026-03-15 19:30:00', 'Plaza Mayor', NULL, 'programada'),
(5, 'Curso Avanzado PATI', 'Especialización en asistencia infantil', 'FORMACION', '2026-04-01 09:00:00', '2026-04-05 14:00:00', 'Centro OVI Castelló', 20, 'programada');

--
-- formador
--
INSERT INTO public.formador VALUES
(1, 'Antonio', 'Gómez Martínez'),
(2, 'Isabel', 'Sánchez Ruiz'),
(3, 'Roberto', 'Navarro Ferrer'),
(4, 'Cristina', 'Serrano Blasco'),
(5, 'David', 'Moliner Cases');

--
-- seleccion (estados en minúscula: propuesta / aceptada / rechazada)
--
INSERT INTO public.seleccion VALUES
(1, 2, '11111111A', '2026-01-26 10:30:00', 'aceptada',  'Candidato seleccionado para entrevista', '2026-01-28 11:45:00'),
(2, 2, '22222222B', '2026-01-26 10:35:00', 'rechazada', 'No disponible en fechas requeridas',     '2026-01-27 09:15:00'),
(3, 5, '33333333C', '2026-01-28 12:00:00', 'propuesta', 'Pendiente de respuesta',                 NULL),
(4, 5, '44444444D', '2026-01-28 12:05:00', 'propuesta', 'Segunda opción',                         NULL),
(5, 1, '55555555E', '2026-01-20 09:00:00', 'rechazada', 'Experiencia insuficiente',               '2026-01-22 14:30:00');

--
-- asistenciaformacion
--
INSERT INTO public.asistenciaformacion VALUES
(1, 1, 1, '2026-03-01', '09:00:00', '14:00:00', true, 'Asistencia completa'),
(2, 2, 1, '2026-03-01', '09:00:00', '13:30:00', true, 'Salió media hora antes'),
(3, 3, 3, '2026-03-10', '10:00:00', '13:00:00', true, 'Asistencia correcta'),
(4, 4, 3, '2026-03-10', '10:15:00', '13:00:00', true, 'Llegó 15 minutos tarde'),
(5, 5, 5, '2026-04-01', '09:00:00', NULL, false, 'Pendiente de finalización');

--
-- comunicacionusuariovipap
--
INSERT INTO public.comunicacionusuariovipap VALUES
(1, 2, '11111111A', '2026-01-27 11:30:00', 'telefono', 'saliente', 'Llamada para concertar entrevista', 'Contacto exitoso'),
(2, 2, '11111111A', '2026-01-28 10:00:00', 'presencial', 'entrante', 'Entrevista en oficina OVI', 'Buena impresión'),
(3, 5, '33333333C', '2026-01-29 16:45:00', 'email', 'saliente', 'Envío de documentación complementaria', 'Pendiente de respuesta'),
(4, 1, '55555555E', '2026-01-21 09:15:00', 'email', 'entrante', 'Consulta sobre requisitos del puesto', 'Respondido por técnico'),
(5, 2, '22222222B', '2026-01-26 15:20:00', 'telefono', 'saliente', 'Comunicar rechazo candidatura', 'Usuario informado');

--
-- registrocontrato
--
INSERT INTO public.registrocontrato VALUES
(1, 2, '11111111A', '2026-02-01', '2026-08-01', 'contrato_2_11111111A.pdf', '2026-01-30 12:00:00', 'Contrato inicial 6 meses', 'activo'),
(2, 2, '11111111A', '2026-02-01', NULL, 'anexo_contrato_2.pdf', '2026-01-30 12:30:00', 'Anexo con condiciones específicas', 'activo'),
(3, 5, '44444444D', '2026-02-15', '2026-05-15', 'contrato_5_44444444D.pdf', '2026-02-10 09:45:00', 'Contrato trimestral', 'activo'),
(4, 1, NULL, '2026-01-10', '2026-01-25', 'contrato_1_anulado.pdf', '2026-01-05 11:15:00', 'Contrato cancelado antes de iniciar', 'rescindido'),
(5, 3, '55555555E', '2026-01-15', '2026-07-15', 'contrato_3_55555555E.pdf', '2026-01-12 14:20:00', 'Contrato semestral renovable', 'activo');

--
-- Ajuste de las secuencias al máximo id insertado
--
SELECT pg_catalog.setval('public.usuariovi_id_usuari_seq', 5, true);
SELECT pg_catalog.setval('public.aprequest_id_request_seq', 5, true);
SELECT pg_catalog.setval('public.actividad_formacion_id_actividad_seq', 5, true);
SELECT pg_catalog.setval('public.formador_id_formador_seq', 5, true);
SELECT pg_catalog.setval('public.seleccion_id_seleccion_seq', 5, true);
SELECT pg_catalog.setval('public.asistenciaformacion_id_asistencia_seq', 5, true);
SELECT pg_catalog.setval('public.comunicacionusuariovipap_id_comunicacion_seq', 5, true);
SELECT pg_catalog.setval('public.registrocontrato_id_contrato_seq', 5, true);

-- Fin de los datos de ejemplo
