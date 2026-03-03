--
-- PostgreSQL database dump
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

SET default_tablespace = '';

SET default_table_access_method = heap;

-- =====================================================
-- ELIMINAR TABLAS EXISTENTES (orden inverso a creación)
-- =====================================================
DROP TABLE IF EXISTS public.seleccion CASCADE;
DROP TABLE IF EXISTS public.registrocontrato CASCADE;
DROP TABLE IF EXISTS public.comunicacionusuariovipap CASCADE;
DROP TABLE IF EXISTS public.asistenciaformacion CASCADE;
DROP TABLE IF EXISTS public.formador CASCADE;
DROP TABLE IF EXISTS public.actividad_formacion CASCADE;
DROP TABLE IF EXISTS public.aprequest CASCADE;
DROP TABLE IF EXISTS public.asistentepersonal CASCADE;
DROP TABLE IF EXISTS public.usuariovi CASCADE;

-- =====================================================
-- CREAR TABLAS (orden de dependencias)
-- =====================================================

-- 1. TABLA USUARIO OVI
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

CREATE SEQUENCE public.usuariovi_id_usuari_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.usuariovi_id_usuari_seq OWNED BY public.usuariovi.id_usuari;

ALTER TABLE ONLY public.usuariovi ALTER COLUMN id_usuari SET DEFAULT nextval('public.usuariovi_id_usuari_seq'::regclass);

-- 2. TABLA ASISTENTE PERSONAL
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

-- 3. TABLA APREQUEST (solicitudes)
CREATE TABLE public.aprequest (
    id_request integer NOT NULL,
    usuari_ident character varying(60),
    tipus_servei character varying(10),
    estat character varying(30),
    data_creacio date,
    observacions character varying(300),
    CONSTRAINT ri_aprequest_data CHECK ((data_creacio <= CURRENT_DATE))
);

-- 4. TABLA ACTIVIDAD_FORMACION
CREATE TABLE public.actividad_formacion (
    id_actividad integer NOT NULL,
    titulo character varying(150) NOT NULL,
    descripcion text,
    tipo_actividad character varying(20) NOT NULL,
    fecha_inicio timestamp without time zone NOT NULL,
    fecha_fin timestamp without time zone NOT NULL,
    lugar character varying(150),
    aforo_maximo integer,
    estado character varying(20) DEFAULT 'programada'::character varying NOT NULL,
    CONSTRAINT actividad_formacion_tipo_actividad_check CHECK ((tipo_actividad = ANY (ARRAY['FORMACION'::character varying, 'DIVULGACION'::character varying]))),
    CONSTRAINT actividad_formacion_estado_check CHECK ((estado = ANY (ARRAY['programada'::character varying, 'finalizada'::character varying, 'cancelada'::character varying]))),
    CONSTRAINT actividad_formacion_fechas_check CHECK ((fecha_fin >= fecha_inicio))
);

CREATE SEQUENCE public.actividad_formacion_id_actividad_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.actividad_formacion_id_actividad_seq OWNED BY public.actividad_formacion.id_actividad;
ALTER TABLE ONLY public.actividad_formacion ALTER COLUMN id_actividad SET DEFAULT nextval('public.actividad_formacion_id_actividad_seq'::regclass);

CREATE INDEX idx_actividad_tipo ON public.actividad_formacion(tipo_actividad);
CREATE INDEX idx_actividad_fechas ON public.actividad_formacion(fecha_inicio, fecha_fin);

-- 5. TABLA FORMADOR
CREATE TABLE public.formador (
    id_formador integer NOT NULL,
    nombre character varying(80) NOT NULL,
    apellidos character varying(120) NOT NULL
);

CREATE SEQUENCE public.formador_id_formador_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.formador_id_formador_seq OWNED BY public.formador.id_formador;
ALTER TABLE ONLY public.formador ALTER COLUMN id_formador SET DEFAULT nextval('public.formador_id_formador_seq'::regclass);

-- 6. TABLA SELECCION
CREATE TABLE public.seleccion (
    id_seleccion integer NOT NULL,
    id_solicitud integer NOT NULL,
    id_candidato character varying(15) NOT NULL,
    fecha_propuesta timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    estado character varying(20) DEFAULT 'propuesta'::character varying NOT NULL,
    observaciones text,
    fecha_respuesta timestamp without time zone,
    CONSTRAINT seleccion_estado_check CHECK ((estado = ANY (ARRAY['propuesta'::character varying, 'aceptada'::character varying, 'rechazada'::character varying])))
);

CREATE SEQUENCE public.seleccion_id_seleccion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.seleccion_id_seleccion_seq OWNED BY public.seleccion.id_seleccion;
ALTER TABLE ONLY public.seleccion ALTER COLUMN id_seleccion SET DEFAULT nextval('public.seleccion_id_seleccion_seq'::regclass);

CREATE INDEX idx_seleccion_estado ON public.seleccion(estado);

-- 7. TABLA ASISTENCIAFORMACION
CREATE TABLE public.asistenciaformacion (
    id_asistencia integer NOT NULL,
    id_usuari integer,
    id_actividad integer,
    fecha_asistencia date,
    hora_entrada time without time zone,
    hora_salida time without time zone,
    certificado_emitido boolean DEFAULT false,
    observaciones character varying(300),
    CONSTRAINT ri_fechas_asistencia CHECK (((hora_salida IS NULL) OR (hora_salida > hora_entrada)))
);

CREATE SEQUENCE public.asistenciaformacion_id_asistencia_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.asistenciaformacion_id_asistencia_seq OWNED BY public.asistenciaformacion.id_asistencia;
ALTER TABLE ONLY public.asistenciaformacion ALTER COLUMN id_asistencia SET DEFAULT nextval('public.asistenciaformacion_id_asistencia_seq'::regclass);

-- 8. TABLA COMUNICACIONUSUARIOVIPAP
CREATE TABLE public.comunicacionusuariovipap (
    id_comunicacion integer NOT NULL,
    id_solicitud integer,
    dni_asistente character varying(15),
    fecha_comunicacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    tipo_comunicacion character varying(30),
    direccion character varying(20),
    resumen text,
    observaciones character varying(300),
    CONSTRAINT ri_tipo_comunicacion CHECK ((tipo_comunicacion = ANY (ARRAY['email'::character varying, 'telefono'::character varying, 'presencial'::character varying, 'videollamada'::character varying]))),
    CONSTRAINT ri_direccion CHECK ((direccion = ANY (ARRAY['saliente'::character varying, 'entrante'::character varying])))
);

CREATE SEQUENCE public.comunicacionusuariovipap_id_comunicacion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.comunicacionusuariovipap_id_comunicacion_seq OWNED BY public.comunicacionusuariovipap.id_comunicacion;
ALTER TABLE ONLY public.comunicacionusuariovipap ALTER COLUMN id_comunicacion SET DEFAULT nextval('public.comunicacionusuariovipap_id_comunicacion_seq'::regclass);

-- 9. TABLA REGISTROCONTRATO
CREATE TABLE public.registrocontrato (
    id_contrato integer NOT NULL,
    id_solicitud integer,
    dni_asistente character varying(15),
    fecha_inicio date NOT NULL,
    fecha_fin date,
    documento_pdf character varying(255),
    fecha_registro timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    observaciones text,
    estado_contrato character varying(30) DEFAULT 'activo'::character varying,
    CONSTRAINT ri_fechas_contrato CHECK (((fecha_fin IS NULL) OR (fecha_fin >= fecha_inicio))),
    CONSTRAINT ri_estado_contrato CHECK ((estado_contrato = ANY (ARRAY['activo'::character varying, 'finalizado'::character varying, 'rescindido'::character varying])))
);

CREATE SEQUENCE public.registrocontrato_id_contrato_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.registrocontrato_id_contrato_seq OWNED BY public.registrocontrato.id_contrato;
ALTER TABLE ONLY public.registrocontrato ALTER COLUMN id_contrato SET DEFAULT nextval('public.registrocontrato_id_contrato_seq'::regclass);

-- =====================================================
-- AÑADIR CONSTRAINTS (claves primarias, únicas y foráneas)
-- =====================================================

-- Claves primarias
ALTER TABLE ONLY public.usuariovi ADD CONSTRAINT usuariovi_pkey PRIMARY KEY (id_usuari);
ALTER TABLE ONLY public.asistentepersonal ADD CONSTRAINT cp_asistente PRIMARY KEY (dni);
ALTER TABLE ONLY public.aprequest ADD CONSTRAINT cp_aprequest PRIMARY KEY (id_request);
ALTER TABLE ONLY public.actividad_formacion ADD CONSTRAINT actividad_formacion_pkey PRIMARY KEY (id_actividad);
ALTER TABLE ONLY public.formador ADD CONSTRAINT formador_pkey PRIMARY KEY (id_formador);
ALTER TABLE ONLY public.seleccion ADD CONSTRAINT seleccion_pkey PRIMARY KEY (id_seleccion);
ALTER TABLE ONLY public.asistenciaformacion ADD CONSTRAINT pk_asistencia PRIMARY KEY (id_asistencia);
ALTER TABLE ONLY public.comunicacionusuariovipap ADD CONSTRAINT pk_comunicacion PRIMARY KEY (id_comunicacion);
ALTER TABLE ONLY public.registrocontrato ADD CONSTRAINT pk_contrato PRIMARY KEY (id_contrato);

-- Claves únicas
ALTER TABLE ONLY public.usuariovi ADD CONSTRAINT usuariovi_identificador_sgovi_key UNIQUE (identificador_sgovi);
ALTER TABLE ONLY public.usuariovi ADD CONSTRAINT usuariovi_email_key UNIQUE (email);
ALTER TABLE ONLY public.usuariovi ADD CONSTRAINT usuariovi_dni_key UNIQUE (dni);
ALTER TABLE ONLY public.asistentepersonal ADD CONSTRAINT calt_asistente_email UNIQUE (email);
ALTER TABLE ONLY public.seleccion ADD CONSTRAINT uk_solicitud_candidato UNIQUE (id_solicitud, id_candidato);

-- Claves foráneas
ALTER TABLE ONLY public.seleccion ADD CONSTRAINT fk_seleccion_solicitud FOREIGN KEY (id_solicitud) REFERENCES public.aprequest(id_request) ON DELETE CASCADE;
ALTER TABLE ONLY public.seleccion ADD CONSTRAINT fk_seleccion_candidato FOREIGN KEY (id_candidato) REFERENCES public.asistentepersonal(dni) ON DELETE CASCADE;
ALTER TABLE ONLY public.asistenciaformacion ADD CONSTRAINT fk_asistencia_usuari FOREIGN KEY (id_usuari) REFERENCES public.usuariovi(id_usuari);
ALTER TABLE ONLY public.asistenciaformacion ADD CONSTRAINT fk_asistencia_actividad FOREIGN KEY (id_actividad) REFERENCES public.actividad_formacion(id_actividad);
ALTER TABLE ONLY public.comunicacionusuariovipap ADD CONSTRAINT fk_comunicacion_solicitud FOREIGN KEY (id_solicitud) REFERENCES public.aprequest(id_request);
ALTER TABLE ONLY public.comunicacionusuariovipap ADD CONSTRAINT fk_comunicacion_asistente FOREIGN KEY (dni_asistente) REFERENCES public.asistentepersonal(dni);
ALTER TABLE ONLY public.registrocontrato ADD CONSTRAINT fk_contrato_solicitud FOREIGN KEY (id_solicitud) REFERENCES public.aprequest(id_request);
ALTER TABLE ONLY public.registrocontrato ADD CONSTRAINT fk_contrato_asistente FOREIGN KEY (dni_asistente) REFERENCES public.asistentepersonal(dni);

-- =====================================================
-- ASIGNAR OWNERS
-- =====================================================
ALTER TABLE public.usuariovi OWNER TO ei102725fgl;
ALTER TABLE public.asistentepersonal OWNER TO ei102725fgl;
ALTER TABLE public.aprequest OWNER TO ei102725fgl;
ALTER TABLE public.actividad_formacion OWNER TO ei102725fgl;
ALTER TABLE public.formador OWNER TO ei102725fgl;
ALTER TABLE public.seleccion OWNER TO ei102725fgl;
ALTER TABLE public.asistenciaformacion OWNER TO ei102725fgl;
ALTER TABLE public.comunicacionusuariovipap OWNER TO ei102725fgl;
ALTER TABLE public.registrocontrato OWNER TO ei102725fgl;

ALTER SEQUENCE public.usuariovi_id_usuari_seq OWNER TO ei102725fgl;
ALTER SEQUENCE public.actividad_formacion_id_actividad_seq OWNER TO ei102725fgl;
ALTER SEQUENCE public.formador_id_formador_seq OWNER TO ei102725fgl;
ALTER SEQUENCE public.seleccion_id_seleccion_seq OWNER TO ei102725fgl;
ALTER SEQUENCE public.asistenciaformacion_id_asistencia_seq OWNER TO ei102725fgl;
ALTER SEQUENCE public.comunicacionusuariovipap_id_comunicacion_seq OWNER TO ei102725fgl;
ALTER SEQUENCE public.registrocontrato_id_contrato_seq OWNER TO ei102725fgl;

--
-- PostgreSQL database dump complete
--
