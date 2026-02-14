-- H2-compatible schema for Stats service

CREATE TABLE IF NOT EXISTS HITS
(
    ID        BIGINT AUTO_INCREMENT PRIMARY KEY,
    APP       VARCHAR(255)    NOT NULL,
    URI       VARCHAR(255)    NOT NULL,
    IP        VARCHAR(50)     NOT NULL,
    HIT_TIME  TIMESTAMP       NOT NULL
);
