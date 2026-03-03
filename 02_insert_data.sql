--
-- PostgreSQL database dump
--

\restrict ZKOBPuk3FFaM5aMd02TqjgRmpVj18fithF3AJ6Ai03mwxSjyGeANRXGXn3hjwGa

-- Dumped from database version 16.9
-- Dumped by pg_dump version 16.10 (Ubuntu 16.10-0ubuntu0.24.04.1)

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
-- Data for Name: aprequest; Type: TABLE DATA; Schema: public; Owner: ei102725fgl
--

INSERT INTO public.aprequest VALUES (1, 'uvi_0010', 'PAP', 'EN_REVISIO', '2026-01-15', 'Sol·licitud inicial');
INSERT INTO public.aprequest VALUES (2, 'uvi_0011', 'PATI', 'APROVADA', '2026-01-18', 'Documentació correcta');
INSERT INTO public.aprequest VALUES (3, 'uvi_0012', 'PAP', 'REBUTJADA', '2026-01-20', 'No compleix els requisits');
INSERT INTO public.aprequest VALUES (4, 'uvi_0013', 'PATI', 'EN_REVISIO', '2026-01-22', 'Pendent de validació');
INSERT INTO public.aprequest VALUES (5, 'uvi_0014', 'PAP', 'APROVADA', '2026-01-25', 'Aprovada provisionalment');


--
-- Data for Name: asistentepersonal; Type: TABLE DATA; Schema: public; Owner: ei102725fgl
--

INSERT INTO public.asistentepersonal VALUES ('11111111K', 'Sofía', 'Molina Cases', 'sofia.molina@email.com', '663890123', 52, 'Trabajo Social', true);
INSERT INTO public.asistentepersonal VALUES ('22222222L', 'Marcos', 'Alcover Tur', 'marcos.alcover@email.com', '664901234', 19, 'Estudiante Enfermería', false);
INSERT INTO public.asistentepersonal VALUES ('33333333M', 'Neus', 'Giner Cervera', 'neus.giner@email.com', '665012345', 27, 'Integración Social', true);
INSERT INTO public.asistentepersonal VALUES ('44444444N', 'Gerardo', 'Pons Amorós', 'gerardo.pons@email.com', '666123456', 41, 'Educador Social', true);
INSERT INTO public.asistentepersonal VALUES ('55555555O', 'Laia', 'Valls Domingo', 'laia.valls@email.com', '667234567', 23, 'Atención Sociosanitaria', false);


--
-- Data for Name: usuariovi; Type: TABLE DATA; Schema: public; Owner: ei102725fgl
--

INSERT INTO public.usuariovi VALUES (1, 'USR001', 'pass1234', 'marta.lopez@email.com', 'Marta', 'López García', '654321987', 'C/ Mayor 23, Castelló', '12345678A', '1985-03-15', true, '2026-02-21 22:19:04.891406', true, NULL, NULL);
INSERT INTO public.usuariovi VALUES (2, 'USR002', 'pass5678', 'joan.martinez@email.com', 'Joan', 'Martínez Ruiz', '678543219', 'Av/ de la Plana 45, Castelló', '87654321B', '1990-07-22', true, '2026-02-21 22:19:04.891406', true, NULL, NULL);
INSERT INTO public.usuariovi VALUES (3, 'USR003', 'pass9012', 'carla.sanchez@email.com', 'Carla', 'Sánchez Pérez', '612345678', 'C/ Colom 12, Castelló', '11223344C', '1978-11-30', true, '2026-02-21 22:19:04.891406', false, NULL, NULL);
INSERT INTO public.usuariovi VALUES (4, 'USR004', 'pass3456', 'pere.ramirez@email.com', 'Pere', 'Ramírez Gómez', '699887766', 'Plça Major 3, Castelló', '22334455D', '1982-09-10', true, '2026-02-21 22:19:04.891406', true, NULL, NULL);
INSERT INTO public.usuariovi VALUES (5, 'USR005', 'pass7890', 'anna.ferrer@email.com', 'Anna', 'Ferrer Navarro', '677889900', 'C/ La Pau 67, Castelló', '33445566E', '1995-05-18', true, '2026-02-21 22:19:04.891406', false, NULL, NULL);


--
-- Name: usuariovi_id_usuari_seq; Type: SEQUENCE SET; Schema: public; Owner: ei102725fgl
--

SELECT pg_catalog.setval('public.usuariovi_id_usuari_seq', 6, true);


--
-- PostgreSQL database dump complete
--

\unrestrict ZKOBPuk3FFaM5aMd02TqjgRmpVj18fithF3AJ6Ai03mwxSjyGeANRXGXn3hjwGa

