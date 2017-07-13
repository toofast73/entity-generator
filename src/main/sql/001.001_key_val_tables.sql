CREATE TABLE KEY_VAL_MAIN (
  ID             NUMBER(18) PRIMARY KEY,
  EDIT_DATE      TIMESTAMP(3)        NOT NULL,
  AS_NAME        VARCHAR2(4000 CHAR) NOT NULL,
  OPERATION_TYPE VARCHAR2(4000 CHAR) NOT NULL,
  OPERATION_ID   NUMBER(18)          NOT NULL
);

CREATE SEQUENCE SEQ_KEY_VAL_MAIN CACHE 100;

CREATE TABLE KEY_VAL_CHILD (
  MAIN_ID NUMBER(18)          NOT NULL,
  KEY     VARCHAR2(4000 CHAR) NOT NULL,
  VALUE   VARCHAR2(4000 CHAR) NOT NULL,

  CONSTRAINT FK_KEY_VAL_MAIN
  FOREIGN KEY (MAIN_ID)
  REFERENCES KEY_VAL_MAIN (ID)
);

CREATE INDEX IDX_KVC_MAIN_ID
  ON KEY_VAL_CHILD (MAIN_ID);

