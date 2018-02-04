--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: adminpack; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION adminpack; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: banished_users; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE banished_users (
  id bigint NOT NULL,
  created_on timestamp without time zone,
  last_modified timestamp without time zone,
  group_id bigint,
  user_email character varying(255)
);


ALTER TABLE banished_users OWNER TO postgres;

--
-- Name: banished_users_aud; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE banished_users_aud (
  id bigint NOT NULL,
  rev integer NOT NULL,
  revtype smallint,
  group_id bigint,
  user_email character varying(255)
);


ALTER TABLE banished_users_aud OWNER TO postgres;

--
-- Name: banished_users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE banished_users_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE banished_users_id_seq OWNER TO postgres;

--
-- Name: banished_users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE banished_users_id_seq OWNED BY banished_users.id;


--
-- Name: group_save; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE group_save (
  id bigint NOT NULL,
  created_on timestamp without time zone,
  last_modified timestamp without time zone,
  url character varying(255),
  accountname character varying(255),
  description character varying(255),
  name character varying(255),
  password oid,
  group_id bigint NOT NULL,
  type_id bigint NOT NULL
);


ALTER TABLE group_save OWNER TO postgres;

--
-- Name: group_save_aud; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE group_save_aud (
  id bigint NOT NULL,
  rev integer NOT NULL,
  revtype smallint,
  url character varying(255),
  accountname character varying(255),
  description character varying(255),
  name character varying(255),
  password oid,
  group_id bigint,
  type_id bigint
);


ALTER TABLE group_save_aud OWNER TO postgres;

--
-- Name: group_save_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE group_save_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE group_save_id_seq OWNER TO postgres;

--
-- Name: group_save_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE group_save_id_seq OWNED BY group_save.id;


--
-- Name: groups; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE groups (
  id bigint NOT NULL,
  created_on timestamp without time zone,
  last_modified timestamp without time zone,
  description character varying(255),
  group_email character varying(255),
  name character varying(255),
  sharedstore oid,
  admin_id bigint
);


ALTER TABLE groups OWNER TO postgres;

--
-- Name: groups_aud; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE groups_aud (
  id bigint NOT NULL,
  rev integer NOT NULL,
  revtype smallint,
  description character varying(255),
  group_email character varying(255),
  name character varying(255),
  sharedstore oid,
  admin_id bigint
);


ALTER TABLE groups_aud OWNER TO postgres;

--
-- Name: groups_group_save; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE groups_group_save (
  groups_id bigint NOT NULL,
  groupsaves_id bigint NOT NULL,
  group_id bigint NOT NULL
);


ALTER TABLE groups_group_save OWNER TO postgres;

--
-- Name: groups_group_save_aud; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE groups_group_save_aud (
  rev integer NOT NULL,
  group_id bigint NOT NULL,
  groupsaves_id bigint NOT NULL,
  revtype smallint
);


ALTER TABLE groups_group_save_aud OWNER TO postgres;

--
-- Name: groups_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE groups_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE groups_id_seq OWNER TO postgres;

--
-- Name: groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE groups_id_seq OWNED BY groups.id;


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE hibernate_sequence
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE hibernate_sequence OWNER TO postgres;

--
-- Name: personal_save; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE personal_save (
  id bigint NOT NULL,
  created_on timestamp without time zone,
  last_modified timestamp without time zone,
  accountname character varying(255),
  description character varying(255),
  name character varying(255),
  password oid,
  url character varying(255),
  type_id bigint NOT NULL,
  user_id bigint NOT NULL
);


ALTER TABLE personal_save OWNER TO postgres;

--
-- Name: personal_save_aud; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE personal_save_aud (
  id bigint NOT NULL,
  rev integer NOT NULL,
  revtype smallint,
  accountname character varying(255),
  description character varying(255),
  name character varying(255),
  password oid,
  url character varying(255),
  type_id bigint,
  user_id bigint
);


ALTER TABLE personal_save_aud OWNER TO postgres;

--
-- Name: personal_save_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE personal_save_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE personal_save_id_seq OWNER TO postgres;

--
-- Name: personal_save_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE personal_save_id_seq OWNED BY personal_save.id;


--
-- Name: revinfo; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE revinfo (
  rev integer NOT NULL,
  revtstmp bigint
);


ALTER TABLE revinfo OWNER TO postgres;

--
-- Name: schema_version; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE schema_version (
  installed_rank integer NOT NULL,
  version character varying(50),
  description character varying(200) NOT NULL,
  type character varying(20) NOT NULL,
  script character varying(1000) NOT NULL,
  checksum integer,
  installed_by character varying(100) NOT NULL,
  installed_on timestamp without time zone DEFAULT now() NOT NULL,
  execution_time integer NOT NULL,
  success boolean NOT NULL
);


ALTER TABLE schema_version OWNER TO postgres;

--
-- Name: test_data; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE test_data (
  value character varying(25) NOT NULL
);


ALTER TABLE test_data OWNER TO postgres;

--
-- Name: type; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE type (
  id bigint NOT NULL,
  created_on timestamp without time zone,
  last_modified timestamp without time zone,
  name character varying(255)
);


ALTER TABLE type OWNER TO postgres;

--
-- Name: type_aud; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE type_aud (
  id bigint NOT NULL,
  rev integer NOT NULL,
  revtype smallint,
  name character varying(255)
);


ALTER TABLE type_aud OWNER TO postgres;

--
-- Name: type_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE type_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE type_id_seq OWNER TO postgres;

--
-- Name: type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE type_id_seq OWNED BY type.id;


--
-- Name: user_groups; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE user_groups (
  user_id bigint NOT NULL,
  group_id bigint NOT NULL
);


ALTER TABLE user_groups OWNER TO postgres;

--
-- Name: user_groups_aud; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE user_groups_aud (
  rev integer NOT NULL,
  user_id bigint NOT NULL,
  group_id bigint NOT NULL,
  revtype smallint
);


ALTER TABLE user_groups_aud OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE users (
  id bigint NOT NULL,
  created_on timestamp without time zone,
  last_modified timestamp without time zone,
  emailSenderService character varying(255),
  keystore oid,
  name character varying(255),
  phone character varying(255)
);


ALTER TABLE users OWNER TO postgres;

--
-- Name: users_aud; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE users_aud (
  id bigint NOT NULL,
  rev integer NOT NULL,
  revtype smallint,
  emailSenderService character varying(255),
  keystore oid,
  name character varying(255),
  phone character varying(255)
);


ALTER TABLE users_aud OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE users_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- Name: users_personal_save; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE users_personal_save (
  users_id bigint NOT NULL,
  saves_id bigint NOT NULL,
  user_id bigint NOT NULL
);


ALTER TABLE users_personal_save OWNER TO postgres;

--
-- Name: users_personal_save_aud; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE users_personal_save_aud (
  rev integer NOT NULL,
  user_id bigint NOT NULL,
  saves_id bigint NOT NULL,
  revtype smallint
);


ALTER TABLE users_personal_save_aud OWNER TO postgres;

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY banished_users ALTER COLUMN id SET DEFAULT nextval('banished_users_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY group_save ALTER COLUMN id SET DEFAULT nextval('group_save_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups ALTER COLUMN id SET DEFAULT nextval('groups_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY personal_save ALTER COLUMN id SET DEFAULT nextval('personal_save_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY type ALTER COLUMN id SET DEFAULT nextval('type_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- Name: banished_users_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY banished_users_aud
ADD CONSTRAINT banished_users_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: banished_users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY banished_users
ADD CONSTRAINT banished_users_pkey PRIMARY KEY (id);


--
-- Name: group_save_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY group_save_aud
ADD CONSTRAINT group_save_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: group_save_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY group_save
ADD CONSTRAINT group_save_pkey PRIMARY KEY (id);


--
-- Name: groups_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY groups_aud
ADD CONSTRAINT groups_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: groups_group_save_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY groups_group_save_aud
ADD CONSTRAINT groups_group_save_aud_pkey PRIMARY KEY (rev, group_id, groupsaves_id);


--
-- Name: groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY groups
ADD CONSTRAINT groups_pkey PRIMARY KEY (id);


--
-- Name: personal_save_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY personal_save_aud
ADD CONSTRAINT personal_save_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: personal_save_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY personal_save
ADD CONSTRAINT personal_save_pkey PRIMARY KEY (id);


--
-- Name: revinfo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY revinfo
ADD CONSTRAINT revinfo_pkey PRIMARY KEY (rev);


--
-- Name: schema_version_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY schema_version
ADD CONSTRAINT schema_version_pk PRIMARY KEY (installed_rank);


--
-- Name: test_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY test_data
ADD CONSTRAINT test_data_pkey PRIMARY KEY (value);


--
-- Name: type_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY type_aud
ADD CONSTRAINT type_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY type
ADD CONSTRAINT type_pkey PRIMARY KEY (id);


--
-- Name: uk_de4chr4cix4ca7l7b81iyd64n; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY groups_group_save
ADD CONSTRAINT uk_de4chr4cix4ca7l7b81iyd64n UNIQUE (groupsaves_id);


--
-- Name: uk_eie9j9ud7ii15na2uuh2xdu2l; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY users_personal_save
ADD CONSTRAINT uk_eie9j9ud7ii15na2uuh2xdu2l UNIQUE (saves_id);


--
-- Name: user_groups_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY user_groups_aud
ADD CONSTRAINT user_groups_aud_pkey PRIMARY KEY (rev, user_id, group_id);


--
-- Name: users_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY users_aud
ADD CONSTRAINT users_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: users_personal_save_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY users_personal_save_aud
ADD CONSTRAINT users_personal_save_aud_pkey PRIMARY KEY (rev, user_id, saves_id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY users
ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: schema_version_s_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX schema_version_s_idx ON schema_version USING btree (success);


--
-- Name: fk2yc6o2offcujto3mh2gyj4su1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY banished_users_aud
ADD CONSTRAINT fk2yc6o2offcujto3mh2gyj4su1 FOREIGN KEY (rev) REFERENCES revinfo(rev);


--
-- Name: fk438i6ypijfdofpd9yf2mvew83; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY group_save
ADD CONSTRAINT fk438i6ypijfdofpd9yf2mvew83 FOREIGN KEY (type_id) REFERENCES type(id);


--
-- Name: fk4a5278ptwr6x3t6xuq7pd20fb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY group_save_aud
ADD CONSTRAINT fk4a5278ptwr6x3t6xuq7pd20fb FOREIGN KEY (rev) REFERENCES revinfo(rev);


--
-- Name: fk532dtos401sqwesxxqwa9s6uv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY personal_save_aud
ADD CONSTRAINT fk532dtos401sqwesxxqwa9s6uv FOREIGN KEY (rev) REFERENCES revinfo(rev);


--
-- Name: fk7b91i10jo4hnulrua86mhtomv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_groups_aud
ADD CONSTRAINT fk7b91i10jo4hnulrua86mhtomv FOREIGN KEY (rev) REFERENCES revinfo(rev);


--
-- Name: fk7iy7acqgkqoym64b25amcvy05; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users_personal_save
ADD CONSTRAINT fk7iy7acqgkqoym64b25amcvy05 FOREIGN KEY (saves_id) REFERENCES personal_save(id);


--
-- Name: fk9ck5xfy0ff89qhy927gu3licu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups_group_save_aud
ADD CONSTRAINT fk9ck5xfy0ff89qhy927gu3licu FOREIGN KEY (rev) REFERENCES revinfo(rev);


--
-- Name: fk9yk651tghdrw7o6b7qfy6ptlo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY group_save
ADD CONSTRAINT fk9yk651tghdrw7o6b7qfy6ptlo FOREIGN KEY (group_id) REFERENCES groups(id);


--
-- Name: fk_3abr2ynym33lsuqbjs2c7uvh5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_groups
ADD CONSTRAINT fk_3abr2ynym33lsuqbjs2c7uvh5 FOREIGN KEY (group_id) REFERENCES groups(id);


--
-- Name: fk_3x5v7o8fqoqwm90gk0l7kh663; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY personal_save
ADD CONSTRAINT fk_3x5v7o8fqoqwm90gk0l7kh663 FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: fk_4gaixs1c5txl231tr37r06lj3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY personal_save
ADD CONSTRAINT fk_4gaixs1c5txl231tr37r06lj3 FOREIGN KEY (type_id) REFERENCES type(id);


--
-- Name: fk_9q6mtrnkwlvvvm944j5rryw59; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY group_save
ADD CONSTRAINT fk_9q6mtrnkwlvvvm944j5rryw59 FOREIGN KEY (type_id) REFERENCES type(id);


--
-- Name: fk_de4chr4cix4ca7l7b81iyd64n; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups_group_save
ADD CONSTRAINT fk_de4chr4cix4ca7l7b81iyd64n FOREIGN KEY (groupsaves_id) REFERENCES group_save(id);


--
-- Name: fk_eie9j9ud7ii15na2uuh2xdu2l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users_personal_save
ADD CONSTRAINT fk_eie9j9ud7ii15na2uuh2xdu2l FOREIGN KEY (saves_id) REFERENCES personal_save(id);


--
-- Name: fk_h0veiwfmy104lrq30j206t0dd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups
ADD CONSTRAINT fk_h0veiwfmy104lrq30j206t0dd FOREIGN KEY (admin_id) REFERENCES users(id);


--
-- Name: fk_j4c0ijxup8kltq2qs38x9s3wg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY group_save
ADD CONSTRAINT fk_j4c0ijxup8kltq2qs38x9s3wg FOREIGN KEY (group_id) REFERENCES groups(id);


--
-- Name: fk_mf719sggsyqv8nh8lrmngiuyw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups_group_save
ADD CONSTRAINT fk_mf719sggsyqv8nh8lrmngiuyw FOREIGN KEY (groups_id) REFERENCES groups(id);


--
-- Name: fk_px2l7vj4t68wleap5h150m4ba; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users_personal_save
ADD CONSTRAINT fk_px2l7vj4t68wleap5h150m4ba FOREIGN KEY (users_id) REFERENCES users(id);


--
-- Name: fk_qhsd8ovn89o4usyr3fgaqkdjt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_groups
ADD CONSTRAINT fk_qhsd8ovn89o4usyr3fgaqkdjt FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: fkb78844lqwh6q1sn9vt2wp4xh8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups_group_save
ADD CONSTRAINT fkb78844lqwh6q1sn9vt2wp4xh8 FOREIGN KEY (groupsaves_id) REFERENCES group_save(id);


--
-- Name: fkbck0g3ohydtx4pimqcb7vlo9j; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users_personal_save_aud
ADD CONSTRAINT fkbck0g3ohydtx4pimqcb7vlo9j FOREIGN KEY (rev) REFERENCES revinfo(rev);


--
-- Name: fkbmm86xa690j7nle5rab2sjkip; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY type_aud
ADD CONSTRAINT fkbmm86xa690j7nle5rab2sjkip FOREIGN KEY (rev) REFERENCES revinfo(rev);


--
-- Name: fkd37bs5u9hvbwljup24b2hin2b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_groups
ADD CONSTRAINT fkd37bs5u9hvbwljup24b2hin2b FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: fkehq8i57p0pw0gc4wyyrvka8m9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY personal_save
ADD CONSTRAINT fkehq8i57p0pw0gc4wyyrvka8m9 FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: fkgagrpsu67efy07w83bmi4ad20; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups_aud
ADD CONSTRAINT fkgagrpsu67efy07w83bmi4ad20 FOREIGN KEY (rev) REFERENCES revinfo(rev);


--
-- Name: fkgbdwtfbhx4sb3ru6n1goxpn7b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups_group_save
ADD CONSTRAINT fkgbdwtfbhx4sb3ru6n1goxpn7b FOREIGN KEY (group_id) REFERENCES groups(id);


--
-- Name: fkimrv69cncmhxrn6kafc9gx62t; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY personal_save
ADD CONSTRAINT fkimrv69cncmhxrn6kafc9gx62t FOREIGN KEY (type_id) REFERENCES type(id);


--
-- Name: fkinrdywgyurfk2ojrfkard4ejn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users_aud
ADD CONSTRAINT fkinrdywgyurfk2ojrfkard4ejn FOREIGN KEY (rev) REFERENCES revinfo(rev);


--
-- Name: fkmrgahbb4w32n9wkjqbipttc87; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_groups
ADD CONSTRAINT fkmrgahbb4w32n9wkjqbipttc87 FOREIGN KEY (group_id) REFERENCES groups(id);


--
-- Name: fksnqhvirasbp2bh1ahns2iqeu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY groups
ADD CONSTRAINT fksnqhvirasbp2bh1ahns2iqeu FOREIGN KEY (admin_id) REFERENCES users(id);


--
-- Name: fksw4x84bxrymwnmqg9jojsx1mi; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users_personal_save
ADD CONSTRAINT fksw4x84bxrymwnmqg9jojsx1mi FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--
