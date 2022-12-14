CREATE TABLE IF NOT EXISTS endpoint_hit (
    hit_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(32) NOT NULL,
    uri VARCHAR(1000) NOT NULL,
    ip VARCHAR(45) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_hit PRIMARY KEY (hit_id)
);
