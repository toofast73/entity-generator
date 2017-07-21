CREATE TABLE CHUNK_MAIN (
  ID             BIGSERIAL PRIMARY KEY,
  EDIT_DATE      TIMESTAMP(3)            NOT NULL,
  AS_NAME        CHARACTER VARYING(4000) NOT NULL,
  OPERATION_TYPE CHARACTER VARYING(4000) NOT NULL,
  OPERATION_ID   BIGINT                  NOT NULL
);

CREATE TABLE CHUNK_CHILD (
  MAIN_ID    BIGINT REFERENCES CHUNK_MAIN (ID),
  CHUNK_NUM  BIGINT                  NOT NULL,
  CHUNK_DATA CHARACTER VARYING(4000) NOT NULL
);

CREATE INDEX IDX_CC_MAIN_ID ON CHUNK_CHILD (MAIN_ID);