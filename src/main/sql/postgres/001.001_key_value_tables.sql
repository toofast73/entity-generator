CREATE TABLE KEY_VAL_MAIN
(
  id             BIGSERIAL PRIMARY KEY,
  edit_date      TIMESTAMP(3)            NOT NULL,
  as_name        CHARACTER VARYING(4000) NOT NULL,
  operation_type CHARACTER VARYING(4000) NOT NULL,
  operation_id   BIGINT                  NOT NULL
);

CREATE TABLE KEY_VAL_CHILD
(
  main_id BIGINT REFERENCES KEY_VAL_MAIN (id),
  key     CHARACTER VARYING(4000) NOT NULL,
  value   CHARACTER VARYING(4000) NOT NULL
);

CREATE INDEX IDX_KVC_MAIN_ID ON KEY_VAL_CHILD (MAIN_ID);