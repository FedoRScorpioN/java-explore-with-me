CREATE TABLE IF NOT EXISTS USERS
(
    ID    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NAME  VARCHAR(50)  NOT NULL,
    EMAIL VARCHAR(320) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS CATEGORIES
(
    ID   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS EVENTS
(
    ID                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ANNOTATION         VARCHAR(3000)               NOT NULL,
    CATEGORY_ID        BIGINT                      NOT NULL,
    CREATED_ON         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    DESCRIPTION        VARCHAR(10000)              NOT NULL,
    EVENT_DATE         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    INITIATOR_ID       BIGINT                      NOT NULL,
    LAT                FLOAT                       NOT NULL,
    LON                FLOAT                       NOT NULL,
    PAID               BOOLEAN                     NOT NULL,
    PARTICIPANT_LIMIT  BIGINT                      NOT NULL,
    PUBLISHED_ON       TIMESTAMP WITHOUT TIME ZONE,
    REQUEST_MODERATION BOOLEAN,
    STATE              VARCHAR(100)                NOT NULL,
    TITLE              VARCHAR(200)                NOT NULL
);
CREATE TABLE IF NOT EXISTS REQUESTS
(
    ID           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    EVENT_ID     BIGINT                      NOT NULL,
    CREATED      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    REQUESTER_ID BIGINT                      NOT NULL,
    STATUS       VARCHAR(100)                NOT NULL,
    CONSTRAINT UQ_REQUESTS UNIQUE (EVENT_ID, REQUESTER_ID)
);
CREATE TABLE IF NOT EXISTS COMPILATIONS
(
    ID     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    TITLE  VARCHAR(128) NOT NULL,
    PINNED BOOLEAN      NOT NULL
);
CREATE TABLE IF NOT EXISTS COMPILATION_EVENT
(
    COMPILATION_ID BIGINT NOT NULL,
    EVENT_ID       BIGINT NOT NULL,
    CONSTRAINT COMPILATION_EVENT_PK PRIMARY KEY (COMPILATION_ID, EVENT_ID)
);