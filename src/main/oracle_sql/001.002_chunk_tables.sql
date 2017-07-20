CREATE TABLE CHUNK_MAIN (
  ID             NUMBER(18) PRIMARY KEY,
  EDIT_DATE      TIMESTAMP(3)        NOT NULL,
  AS_NAME        VARCHAR2(4000 CHAR) NOT NULL,
  OPERATION_TYPE VARCHAR2(4000 CHAR) NOT NULL,
  OPERATION_ID   NUMBER(18)          NOT NULL
);

CREATE SEQUENCE SEQ_CHUNK_MAIN CACHE 100;

CREATE TABLE CHUNK_CHILD (
  MAIN_ID    NUMBER(18)          NOT NULL,
  CHUNK_NUM  NUMBER(18)          NOT NULL,
  CHUNK_DATA VARCHAR2(4000 CHAR) NOT NULL,

  CONSTRAINT FK_CHUNK_CHILD_MAIN
  FOREIGN KEY (MAIN_ID)
  REFERENCES CHUNK_MAIN (ID)
);

CREATE INDEX IDX_CC_MAIN_ID
  ON CHUNK_CHILD (MAIN_ID);
