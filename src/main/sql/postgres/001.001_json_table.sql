CREATE TABLE JSON_TABLE
(
  id             BIGSERIAL PRIMARY KEY,
  edit_date      TIMESTAMP(3)            NOT NULL,
  as_name        CHARACTER VARYING(4000) NOT NULL,
  operation_type CHARACTER VARYING(4000) NOT NULL,
  operation_id   BIGINT                  NOT NULL,
  data_json      JSONB                   NOT NULL
)