CREATE TABLE MAIN_KEY_VAL (
  ID             NUMBER(18) PRIMARY KEY,
  EDIT_DATE      TIMESTAMP(3)        NOT NULL,
  AS_NAME        VARCHAR2(4000 CHAR) NOT NULL,
  OPERATION_TYPE VARCHAR2(4000 CHAR) NOT NULL,
  OPERATION_ID   NUMBER(18)          NOT NULL
);

CREATE SEQUENCE SEQ_MAIN_KEY_VAL CACHE 100;

CREATE TABLE CHILD_KEY_VAL (
  MAIN_ID NUMBER(18)          NOT NULL,
  KEY     VARCHAR2(4000 CHAR) NOT NULL,
  VALUE   VARCHAR2(4000 CHAR) NOT NULL,

  CONSTRAINT FK_KEY_VAL_MAIN
  FOREIGN KEY (MAIN_ID)
  REFERENCES MAIN_KEY_VAL (ID)
);

CREATE INDEX IDX_CKV_MAIN_ID
  ON CHILD_KEY_VAL (MAIN_ID);

