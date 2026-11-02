--
-- PostgreSQL database dump
--

\restrict UF33RDSePZN1frKis9eh49NmATnT3qdJLAL26dNc1ZyGfoABDW41i7BdLLygLa6

-- Dumped from database version 17.11 (Debian 17.11-1.pgdg12+2)
-- Dumped by pg_dump version 17.11 (Debian 17.11-1.pgdg12+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: vector; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public;


--
-- Name: EXTENSION vector; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION vector IS 'vector data type and ivfflat and hnsw access methods';


--
-- Name: estado_meta; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.estado_meta AS ENUM (
    'INICIADA',
    'EN_PROCESO',
    'PAUSADA',
    'REANUDADA',
    'COMPLETADA',
    'CANCELADA'
);


--
-- Name: estado_usuario; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.estado_usuario AS ENUM (
    'PENDIENTE',
    'ACTIVO',
    'SUSPENDIDO',
    'INACTIVO',
    'ELIMINADO'
);


--
-- Name: frecuencia_suscripcion; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.frecuencia_suscripcion AS ENUM (
    'MENSUAL',
    'ANUAL',
    'SEMANAL'
);


--
-- Name: rol_usuario; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.rol_usuario AS ENUM (
    'USER',
    'ADMIN'
);


--
-- Name: tipo_categoria; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.tipo_categoria AS ENUM (
    'INGRESOS',
    'GASTOS',
    'AHORRO'
);


--
-- Name: tipo_cuenta; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.tipo_cuenta AS ENUM (
    'EFECTIVO',
    'BANCO',
    'BILLETERA_DIGITAL',
    'OTRO'
);


--
-- Name: tipo_movimiento; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.tipo_movimiento AS ENUM (
    'INGRESO',
    'EGRESO',
    'AHORRO'
);


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: aportes_meta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.aportes_meta (
    id uuid NOT NULL,
    meta_id uuid NOT NULL,
    movimiento_id uuid NOT NULL,
    monto numeric(19,2) NOT NULL,
    fecha date DEFAULT CURRENT_DATE NOT NULL,
    descripcion text,
    CONSTRAINT chk_monto_aporte_positivo CHECK ((monto > (0)::numeric))
);


--
-- Name: categorias; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.categorias (
    id uuid NOT NULL,
    activo boolean NOT NULL,
    fecha_creacion date NOT NULL,
    nombre character varying(255) NOT NULL,
    categoria_padre_id uuid,
    tipo public.tipo_categoria NOT NULL,
    fecha_actualizacion date,
    usuario_id uuid
);


--
-- Name: cuentas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cuentas (
    id uuid NOT NULL,
    activo boolean NOT NULL,
    fecha_creacion date NOT NULL,
    saldo_actual numeric(14,2) NOT NULL,
    saldo_inicial numeric(14,2) NOT NULL,
    nombre character varying(80) NOT NULL,
    tipo public.tipo_cuenta NOT NULL,
    fecha_actualizacion date,
    usuario_id uuid NOT NULL
);


--
-- Name: metas_financieras; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.metas_financieras (
    id uuid NOT NULL,
    usuario_id uuid NOT NULL,
    nombre character varying(255) NOT NULL,
    descripcion text,
    monto_actual numeric(19,2) DEFAULT 0.00 NOT NULL,
    monto_objetivo numeric(19,2) NOT NULL,
    progreso double precision DEFAULT 0.0 NOT NULL,
    fecha_inicio date DEFAULT CURRENT_DATE NOT NULL,
    fecha_objetivo date NOT NULL,
    estado public.estado_meta NOT NULL,
    ultima_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_fechas_coherentes CHECK ((fecha_objetivo >= fecha_inicio)),
    CONSTRAINT chk_monto_actual_no_negativo CHECK ((monto_actual >= (0)::numeric)),
    CONSTRAINT chk_monto_positivo CHECK ((monto_objetivo > (0)::numeric)),
    CONSTRAINT chk_porcentaje_rango CHECK (((progreso >= (0.0)::double precision) AND (progreso <= (100.0)::double precision)))
);


--
-- Name: movimientos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.movimientos (
    id uuid NOT NULL,
    monto numeric(19,2) NOT NULL,
    fecha_registro timestamp(6) without time zone NOT NULL,
    descripcion character varying(255),
    tipo public.tipo_movimiento NOT NULL,
    url_comprobante text,
    fecha_actualizacion timestamp(6) without time zone,
    cuenta_id uuid NOT NULL,
    categoria_id uuid NOT NULL,
    usuario_id uuid NOT NULL,
    frecuentes_id uuid
);


--
-- Name: movimientos_frecuentes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.movimientos_frecuentes (
    id uuid NOT NULL,
    usuario_id uuid NOT NULL,
    cuenta_id uuid NOT NULL,
    categoria_id uuid NOT NULL,
    nombre character varying(255) NOT NULL,
    monto numeric(19,2) NOT NULL,
    frecuencia_cobro public.frecuencia_suscripcion NOT NULL,
    tipo public.tipo_movimiento NOT NULL,
    dia_pago integer NOT NULL,
    siguiente_pago date NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    CONSTRAINT chk_movimientos_frecuentes_dia_pago CHECK (((dia_pago >= 1) AND (dia_pago <= 31))),
    CONSTRAINT chk_movimientos_frecuentes_monto CHECK ((monto > (0)::numeric))
);


--
-- Name: presupuesto_categorias; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.presupuesto_categorias (
    id uuid NOT NULL,
    presupuesto_id uuid NOT NULL,
    categoria_id uuid NOT NULL,
    monto_limite numeric(19,2) NOT NULL,
    CONSTRAINT presupuesto_categorias_monto_limite_check CHECK ((monto_limite > (0)::numeric))
);


--
-- Name: presupuestos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.presupuestos (
    id uuid NOT NULL,
    usuario_id uuid NOT NULL,
    nombre character varying(255) NOT NULL,
    periodo_inicial date NOT NULL,
    periodo_fin date NOT NULL,
    monto_limite_total numeric(19,2) NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_actualizado timestamp without time zone,
    CONSTRAINT chk_presupuestos_periodo CHECK ((periodo_inicial <= periodo_fin)),
    CONSTRAINT presupuestos_monto_limite_total_check CHECK ((monto_limite_total > (0)::numeric))
);


--
-- Name: transferencias; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.transferencias (
    id uuid NOT NULL,
    monto numeric(14,2) NOT NULL,
    fecha_creacion date NOT NULL,
    descripcion character varying(255),
    cuenta_destino_id uuid NOT NULL,
    cuenta_origen_id uuid NOT NULL,
    usuario_id uuid NOT NULL
);


--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuarios (
    id uuid NOT NULL,
    fecha_creacion date NOT NULL,
    email character varying(255) NOT NULL,
    enable2fa boolean NOT NULL,
    apellido character varying(255) NOT NULL,
    nombre character varying(255) NOT NULL,
    "contraseña" character varying(255) NOT NULL,
    rol public.rol_usuario NOT NULL,
    secret2fa character varying(255),
    estado public.estado_usuario NOT NULL,
    fecha_actualizacion date
);


--
-- Data for Name: aportes_meta; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.aportes_meta (id, meta_id, movimiento_id, monto, fecha, descripcion) FROM stdin;
f0276b60-5a7b-4320-aa8b-3931800255ed	22ab5d91-4c62-47c1-a541-2bfe1d863b5f	70308d17-fd1f-4ee8-b829-b4f18561701e	700.00	2026-10-23	guarde 700 soles x5
\.


--
-- Data for Name: categorias; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.categorias (id, activo, fecha_creacion, nombre, categoria_padre_id, tipo, fecha_actualizacion, usuario_id) FROM stdin;
92897f69-873d-4c3b-857b-4078c8f0d993	t	2026-10-23	Transporte	\N	GASTOS	\N	\N
def23cbf-8d1f-480c-9cca-4e98adddbfeb	t	2026-10-23	Alimentacion	\N	GASTOS	\N	\N
2b6dbcd3-11f3-4dc4-aeac-ede7af41007e	t	2026-10-23	Servicios	\N	GASTOS	\N	\N
ed34be52-fbd0-4d81-b80f-7dcf07a90fed	t	2026-10-23	Sueldo	\N	INGRESOS	\N	\N
7feaaff3-138d-485c-a09f-af957549eae5	t	2026-10-23	Educación	2b6dbcd3-11f3-4dc4-aeac-ede7af41007e	GASTOS	\N	28825d62-3979-4819-bf88-1f126a9cefdb
fb84ff2d-1f7a-45c2-8382-f0688cff48ad	t	2026-10-23	Aportes a Metas Financieras	\N	AHORRO	\N	\N
\.


--
-- Data for Name: cuentas; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cuentas (id, activo, fecha_creacion, saldo_actual, saldo_inicial, nombre, tipo, fecha_actualizacion, usuario_id) FROM stdin;
add4a967-d03f-4d9a-8b9e-0eaa239fd54d	t	2026-10-23	4340.00	5000.00	Yape	BILLETERA_DIGITAL	2026-10-23	28825d62-3979-4819-bf88-1f126a9cefdb
7230a647-990f-45da-bfae-29741f2e7a64	t	2026-10-23	9928.00	10000.00	Interback	BANCO	2026-10-25	28825d62-3979-4819-bf88-1f126a9cefdb
5e4bf555-5852-4d65-a759-fdc1902e2114	t	2026-10-23	7238.69	8000.00	Plin	BILLETERA_DIGITAL	2026-10-25	28825d62-3979-4819-bf88-1f126a9cefdb
\.


--
-- Data for Name: metas_financieras; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.metas_financieras (id, usuario_id, nombre, descripcion, monto_actual, monto_objetivo, progreso, fecha_inicio, fecha_objetivo, estado, ultima_actualizacion) FROM stdin;
22ab5d91-4c62-47c1-a541-2bfe1d863b5f	28825d62-3979-4819-bf88-1f126a9cefdb	Laptop Gamer 	Laptop para el estudio y trabajo (en realidad para jugar xd)	700.00	5000.00	14	2026-10-23	2026-12-15	EN_PROCESO	2026-10-23 23:00:32.838486
\.


--
-- Data for Name: movimientos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.movimientos (id, monto, fecha_registro, descripcion, tipo, url_comprobante, fecha_actualizacion, cuenta_id, categoria_id, usuario_id, frecuentes_id) FROM stdin;
a8d775f6-dd82-4c04-8475-7fa300f7de98	2.50	2026-10-23 22:49:59.633124	pago de transporte publico	EGRESO	\N	\N	5e4bf555-5852-4d65-a759-fdc1902e2114	92897f69-873d-4c3b-857b-4078c8f0d993	28825d62-3979-4819-bf88-1f126a9cefdb	\N
0ca673e4-012a-434f-b0b0-a76fe8fadf13	790.81	2026-10-23 22:46:50.587987	Pago de pensiones universitarias en UTP realizado en Agente BCP.	EGRESO	https://res.cloudinary.com/dep5zhuay/image/upload/v1792813862/ijzow12v9ineiofm1nsp.jpg	\N	5e4bf555-5852-4d65-a759-fdc1902e2114	7feaaff3-138d-485c-a09f-af957549eae5	28825d62-3979-4819-bf88-1f126a9cefdb	\N
70308d17-fd1f-4ee8-b829-b4f18561701e	700.00	2026-10-23 23:00:32.82577	guarde 700 soles x5	AHORRO	\N	\N	add4a967-d03f-4d9a-8b9e-0eaa239fd54d	fb84ff2d-1f7a-45c2-8382-f0688cff48ad	28825d62-3979-4819-bf88-1f126a9cefdb	\N
70308d17-fd1f-4ee8-b829-b4f185617019	500.00	2026-09-23 22:46:50.587987	prueba de periodo	EGRESO	\N	\N	add4a967-d03f-4d9a-8b9e-0eaa239fd54d	fb84ff2d-1f7a-45c2-8382-f0688cff48ad	28825d62-3979-4819-bf88-1f126a9cefdb	\N
0ca673e4-012a-434f-b0b0-a76fe8fadf87	2500.00	2026-10-25 00:00:00	sueldo de prueba	INGRESO	\N	\N	5e4bf555-5852-4d65-a759-fdc1902e2114	ed34be52-fbd0-4d81-b80f-7dcf07a90fed	28825d62-3979-4819-bf88-1f126a9cefdb	78cd5e15-68b0-4d25-8dd7-99e0f0a32331
\.


--
-- Data for Name: movimientos_frecuentes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.movimientos_frecuentes (id, usuario_id, cuenta_id, categoria_id, nombre, monto, frecuencia_cobro, tipo, dia_pago, siguiente_pago, activo) FROM stdin;
78cd5e15-68b0-4d25-8dd7-99e0f0a32331	28825d62-3979-4819-bf88-1f126a9cefdb	7230a647-990f-45da-bfae-29741f2e7a64	ed34be52-fbd0-4d81-b80f-7dcf07a90fed	Sueldo del trabajo	2500.00	MENSUAL	INGRESO	15	2026-11-15	t
\.


--
-- Data for Name: presupuesto_categorias; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.presupuesto_categorias (id, presupuesto_id, categoria_id, monto_limite) FROM stdin;
264c3615-a78d-492c-aad7-a7ee37b21ff2	c5eae4e6-2954-4dfe-a2da-8c1007e3b70d	def23cbf-8d1f-480c-9cca-4e98adddbfeb	1000.00
af99addc-3f10-4e09-8f14-bf4c8993224f	c5eae4e6-2954-4dfe-a2da-8c1007e3b70d	92897f69-873d-4c3b-857b-4078c8f0d993	800.00
\.


--
-- Data for Name: presupuestos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.presupuestos (id, usuario_id, nombre, periodo_inicial, periodo_fin, monto_limite_total, activo, fecha_creacion, fecha_actualizado) FROM stdin;
c5eae4e6-2954-4dfe-a2da-8c1007e3b70d	28825d62-3979-4819-bf88-1f126a9cefdb	Presupuesto Noviembre 2026	2026-11-01	2026-11-30	2500.00	t	2026-10-23 23:03:08.103522	\N
\.


--
-- Data for Name: transferencias; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.transferencias (id, monto, fecha_creacion, descripcion, cuenta_destino_id, cuenta_origen_id, usuario_id) FROM stdin;
0b47e042-2473-46b8-869b-504f96255717	20.00	2026-10-23	Transferencia de PLIN a YAPE	add4a967-d03f-4d9a-8b9e-0eaa239fd54d	5e4bf555-5852-4d65-a759-fdc1902e2114	28825d62-3979-4819-bf88-1f126a9cefdb
eb37cf85-2938-4d78-9b1a-807a995909cd	20.00	2026-10-23	Transferencia de Interback a YAPE	add4a967-d03f-4d9a-8b9e-0eaa239fd54d	7230a647-990f-45da-bfae-29741f2e7a64	28825d62-3979-4819-bf88-1f126a9cefdb
5eb91d1e-e26a-44d6-8f03-233dc57d08b0	21.00	2026-10-24	Transferencia de Interback a PLIN	5e4bf555-5852-4d65-a759-fdc1902e2114	7230a647-990f-45da-bfae-29741f2e7a64	28825d62-3979-4819-bf88-1f126a9cefdb
6b32c723-095f-4161-9658-87a22f24674e	1.00	2026-10-24	Transferencia de Interback a PLIN x3	5e4bf555-5852-4d65-a759-fdc1902e2114	7230a647-990f-45da-bfae-29741f2e7a64	28825d62-3979-4819-bf88-1f126a9cefdb
d89170ec-c879-4aad-82d6-85948ed3316c	15.00	2026-10-24	Transferencia de Interback a PLIN x3	5e4bf555-5852-4d65-a759-fdc1902e2114	7230a647-990f-45da-bfae-29741f2e7a64	28825d62-3979-4819-bf88-1f126a9cefdb
9c4ca5a3-9342-4c3d-bfaa-8b6ceb0d5fb1	15.00	2026-10-25	Transferencia de Interback a PLIN x4	5e4bf555-5852-4d65-a759-fdc1902e2114	7230a647-990f-45da-bfae-29741f2e7a64	28825d62-3979-4819-bf88-1f126a9cefdb
\.


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.usuarios (id, fecha_creacion, email, enable2fa, apellido, nombre, "contraseña", rol, secret2fa, estado, fecha_actualizacion) FROM stdin;
dc6c9d4b-7e92-4c35-88e7-36cf4fef7d27	2026-10-23	admin@admin.com	f	principal	admin	$2a$10$KlEXR21H1b5d2Xr78yPAQ.w4aZn.ImmyUFt7UE8igaCK88fp33b92	ADMIN	\N	ACTIVO	\N
28825d62-3979-4819-bf88-1f126a9cefdb	2026-10-23	cristian@gmail.com	f	adriano	cristian	$2a$10$zsd/qB9hVDUb61tn56XEwu6nL53H2D3L70PguqOJY0.DXCu/39xrW	USER	\N	ACTIVO	\N
\.


--
-- Name: aportes_meta aportes_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aportes_meta
    ADD CONSTRAINT aportes_meta_pkey PRIMARY KEY (id);


--
-- Name: categorias categorias_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);


--
-- Name: cuentas cuentas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cuentas
    ADD CONSTRAINT cuentas_pkey PRIMARY KEY (id);


--
-- Name: metas_financieras metas_financieras_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.metas_financieras
    ADD CONSTRAINT metas_financieras_pkey PRIMARY KEY (id);


--
-- Name: movimientos_frecuentes movimientos_frecuentes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos_frecuentes
    ADD CONSTRAINT movimientos_frecuentes_pkey PRIMARY KEY (id);


--
-- Name: movimientos movimientos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT movimientos_pkey PRIMARY KEY (id);


--
-- Name: presupuesto_categorias presupuesto_categorias_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presupuesto_categorias
    ADD CONSTRAINT presupuesto_categorias_pkey PRIMARY KEY (id);


--
-- Name: presupuestos presupuestos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presupuestos
    ADD CONSTRAINT presupuestos_pkey PRIMARY KEY (id);


--
-- Name: transferencias transferencias_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transferencias
    ADD CONSTRAINT transferencias_pkey PRIMARY KEY (id);


--
-- Name: presupuesto_categorias uq_presupuesto_categoria; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presupuesto_categorias
    ADD CONSTRAINT uq_presupuesto_categoria UNIQUE (presupuesto_id, categoria_id);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: idx_aportes_meta_meta_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_aportes_meta_meta_id ON public.aportes_meta USING btree (meta_id);


--
-- Name: idx_aportes_meta_movimiento_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_aportes_meta_movimiento_id ON public.aportes_meta USING btree (movimiento_id);


--
-- Name: idx_metas_financieras_estado_fecha; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_metas_financieras_estado_fecha ON public.metas_financieras USING btree (estado, fecha_objetivo);


--
-- Name: idx_metas_financieras_usuario_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_metas_financieras_usuario_id ON public.metas_financieras USING btree (usuario_id);


--
-- Name: idx_movimientos_frecuentes_categoria_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_movimientos_frecuentes_categoria_id ON public.movimientos_frecuentes USING btree (categoria_id);


--
-- Name: idx_movimientos_frecuentes_cuenta_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_movimientos_frecuentes_cuenta_id ON public.movimientos_frecuentes USING btree (cuenta_id);


--
-- Name: idx_movimientos_frecuentes_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_movimientos_frecuentes_id ON public.movimientos USING btree (frecuentes_id);


--
-- Name: idx_movimientos_frecuentes_usuario_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_movimientos_frecuentes_usuario_id ON public.movimientos_frecuentes USING btree (usuario_id);


--
-- Name: idx_presupuesto_categorias_categoria; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_presupuesto_categorias_categoria ON public.presupuesto_categorias USING btree (categoria_id);


--
-- Name: idx_presupuestos_usuario_periodo; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_presupuestos_usuario_periodo ON public.presupuestos USING btree (usuario_id, periodo_inicial, periodo_fin);


--
-- Name: idx_usuarios_email; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_usuarios_email ON public.usuarios USING btree (email);


--
-- Name: aportes_meta fk_aportes_meta_meta_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aportes_meta
    ADD CONSTRAINT fk_aportes_meta_meta_id FOREIGN KEY (meta_id) REFERENCES public.metas_financieras(id) ON DELETE CASCADE;


--
-- Name: aportes_meta fk_aportes_meta_movimiento_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.aportes_meta
    ADD CONSTRAINT fk_aportes_meta_movimiento_id FOREIGN KEY (movimiento_id) REFERENCES public.movimientos(id) ON DELETE RESTRICT;


--
-- Name: categorias fk_categorias_padre; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT fk_categorias_padre FOREIGN KEY (categoria_padre_id) REFERENCES public.categorias(id);


--
-- Name: categorias fk_categorias_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT fk_categorias_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: cuentas fk_cuentas_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cuentas
    ADD CONSTRAINT fk_cuentas_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: metas_financieras fk_metas_financieras_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.metas_financieras
    ADD CONSTRAINT fk_metas_financieras_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: movimientos fk_movimientos_categoria; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT fk_movimientos_categoria FOREIGN KEY (categoria_id) REFERENCES public.categorias(id);


--
-- Name: movimientos fk_movimientos_cuenta; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT fk_movimientos_cuenta FOREIGN KEY (cuenta_id) REFERENCES public.cuentas(id);


--
-- Name: movimientos_frecuentes fk_movimientos_frecuentes_categoria; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos_frecuentes
    ADD CONSTRAINT fk_movimientos_frecuentes_categoria FOREIGN KEY (categoria_id) REFERENCES public.categorias(id);


--
-- Name: movimientos_frecuentes fk_movimientos_frecuentes_cuenta; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos_frecuentes
    ADD CONSTRAINT fk_movimientos_frecuentes_cuenta FOREIGN KEY (cuenta_id) REFERENCES public.cuentas(id);


--
-- Name: movimientos_frecuentes fk_movimientos_frecuentes_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos_frecuentes
    ADD CONSTRAINT fk_movimientos_frecuentes_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: movimientos fk_movimientos_suscripcion; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT fk_movimientos_suscripcion FOREIGN KEY (frecuentes_id) REFERENCES public.movimientos_frecuentes(id);


--
-- Name: movimientos fk_movimientos_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT fk_movimientos_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: transferencias fk_transferencias_cuenta_destino; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transferencias
    ADD CONSTRAINT fk_transferencias_cuenta_destino FOREIGN KEY (cuenta_destino_id) REFERENCES public.cuentas(id);


--
-- Name: transferencias fk_transferencias_cuenta_origen; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transferencias
    ADD CONSTRAINT fk_transferencias_cuenta_origen FOREIGN KEY (cuenta_origen_id) REFERENCES public.cuentas(id);


--
-- Name: transferencias fk_transferencias_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.transferencias
    ADD CONSTRAINT fk_transferencias_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- Name: presupuesto_categorias presupuesto_categorias_categoria_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presupuesto_categorias
    ADD CONSTRAINT presupuesto_categorias_categoria_id_fkey FOREIGN KEY (categoria_id) REFERENCES public.categorias(id);


--
-- Name: presupuesto_categorias presupuesto_categorias_presupuesto_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presupuesto_categorias
    ADD CONSTRAINT presupuesto_categorias_presupuesto_id_fkey FOREIGN KEY (presupuesto_id) REFERENCES public.presupuestos(id) ON DELETE CASCADE;


--
-- Name: presupuestos presupuestos_usuario_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.presupuestos
    ADD CONSTRAINT presupuestos_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- PostgreSQL database dump complete
--

\unrestrict UF33RDSePZN1frKis9eh49NmATnT3qdJLAL26dNc1ZyGfoABDW41i7BdLLygLa6

