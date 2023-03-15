CREATE TABLE if NOT EXISTS HITS
(
    ID        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    APP       VARCHAR(255)                NOT NULL,
    URI       VARCHAR(255)                NOT NULL,
    IP        VARCHAR(255)                NOT NULL,
    TIMESTAMP TIMESTAMP WITHOUT TIME ZONE NOT NULL
);