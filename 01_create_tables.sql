--
-- PostgreSQL database dump
--

\restrict 19IQ85Jpi4wVItXLJIpEwiNv4SvHPMcI8kCsDhDVXvzDMNAr5s6g4teP50KDhcv

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: aprequest; Type: TABLE; Schema: public; Owner: ei102725fgl
--

CREATE TABLE public.aprequest (
    id_request integer NOT NULL,
    usuari_ident character varying(60),
    tipus_servei character varying(10),
    estat character varying(30),
    data_creacio date,
    observacions character varying(300),
    CONSTRAINT ri_aprequest_data CHECK ((data_creacio <= CURRENT_DATE))
);


ALTER TABLE public.aprequest OWNER TO ei102725fgl;

--
-- Name: asistentepersonal; Type: TABLE; Schema: public; Owner: ei102725fgl
--

CREATE TABLE public.asistentepersonal (
    dni character varying(15) NOT NULL,
    nombre character varying(50),
    apellidos character varying(100),
    email character varying(100),
    telefono character varying(15),
    edad integer,
    titulacion character varying(100),
    estado boolean,
    CONSTRAINT ri_asistent_edad CHECK ((edad >= 18))
);


ALTER TABLE public.asistentepersonal OWNER TO ei102725fgl;

--
-- Name: usuariovi; Type: TABLE; Schema: public; Owner: ei102725fgl
--

CREATE TABLE public.usuariovi (
    id_usuari integer NOT NULL,
    identificador_sgovi character varying(50) NOT NULL,
    contrasenya character varying(255) NOT NULL,
    email character varying(100) NOT NULL,
    nom character varying(100) NOT NULL,
    cognoms character varying(200) NOT NULL,
    telefon character varying(20),
    adreca text,
    dni character varying(15) NOT NULL,
    data_naixement date,
    consentiment_informat boolean DEFAULT false,
    data_registre timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    estat_tecnic_acceptat boolean DEFAULT false,
    tutor_legal_nom character varying(200),
    tutor_legal_contacte character varying(100),
    CONSTRAINT consentiment_obligatori CHECK ((consentiment_informat = true)),
    CONSTRAINT major_edat CHECK (((data_naixement <= (CURRENT_DATE - '18 years'::interval)) OR (tutor_legal_nom IS NOT NULL)))
);


ALTER TABLE public.usuariovi OWNER TO ei102725fgl;

--
-- Name: usuariovi_id_usuari_seq; Type: SEQUENCE; Schema: public; Owner: ei102725fgl
--

CREATE SEQUENCE public.usuariovi_id_usuari_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usuariovi_id_usuari_seq OWNER TO ei102725fgl;

--
-- Name: usuariovi_id_usuari_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ei102725fgl
--

ALTER SEQUENCE public.usuariovi_id_usuari_seq OWNED BY public.usuariovi.id_usuari;


--
-- Name: usuariovi id_usuari; Type: DEFAULT; Schema: public; Owner: ei102725fgl
--

ALTER TABLE ONLY public.usuariovi ALTER COLUMN id_usuari SET DEFAULT nextval('public.usuariovi_id_usuari_seq'::regclass);


--
-- Name: asistentepersonal calt_asistente_email; Type: CONSTRAINT; Schema: public; Owner: ei102725fgl
--

ALTER TABLE ONLY public.asistentepersonal
    ADD CONSTRAINT calt_asistente_email UNIQUE (email);


--
-- Name: aprequest cp_aprequest; Type: CONSTRAINT; Schema: public; Owner: ei102725fgl
--

ALTER TABLE ONLY public.aprequest
    ADD CONSTRAINT cp_aprequest PRIMARY KEY (id_request);


--
-- Name: asistentepersonal cp_asistente; Type: CONSTRAINT; Schema: public; Owner: ei102725fgl
--

ALTER TABLE ONLY public.asistentepersonal
    ADD CONSTRAINT cp_asistente PRIMARY KEY (dni);


--
-- Name: usuariovi usuariovi_dni_key; Type: CONSTRAINT; Schema: public; Owner: ei102725fgl
--

ALTER TABLE ONLY public.usuariovi
    ADD CONSTRAINT usuariovi_dni_key UNIQUE (dni);


--
-- Name: usuariovi usuariovi_email_key; Type: CONSTRAINT; Schema: public; Owner: ei102725fgl
--

ALTER TABLE ONLY public.usuariovi
    ADD CONSTRAINT usuariovi_email_key UNIQUE (email);


--
-- Name: usuariovi usuariovi_identificador_sgovi_key; Type: CONSTRAINT; Schema: public; Owner: ei102725fgl
--

ALTER TABLE ONLY public.usuariovi
    ADD CONSTRAINT usuariovi_identificador_sgovi_key UNIQUE (identificador_sgovi);


--
-- Name: usuariovi usuariovi_pkey; Type: CONSTRAINT; Schema: public; Owner: ei102725fgl
--

ALTER TABLE ONLY public.usuariovi
    ADD CONSTRAINT usuariovi_pkey PRIMARY KEY (id_usuari);


--
-- PostgreSQL database dump complete
--

\unrestrict 19IQ85Jpi4wVItXLJIpEwiNv4SvHPMcI8kCsDhDVXvzDMNAr5s6g4teP50KDhcv

