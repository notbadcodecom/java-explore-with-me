CREATE TABLE users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE compilations (
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(256) NOT NULL,
    pinned BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_compilation PRIMARY KEY (compilation_id)
);

CREATE TABLE location (
    location_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(128) NOT NULL,
    lat NUMERIC(7, 5) NOT NULL,
    lon NUMERIC(8, 5) NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (location_id)
);

CREATE TABLE categories (
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(128) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (category_id)
);

CREATE TABLE events (
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(1024) NOT NULL,
    title VARCHAR(128) NOT NULL,
    description VARCHAR(2048) NOT NULL,
    state VARCHAR(64) NOT NULL,
    created TIMESTAMP DEFAULT NOW(),
    published TIMESTAMP,
    initiator_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    paid BOOLEAN DEFAULT FALSE,
    participant_limit INT NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (event_id),
    CONSTRAINT fk_initiator FOREIGN KEY (initiator_id)
        REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_location FOREIGN KEY (location_id)
        REFERENCES location (location_id) ON DELETE SET NULL,
    CONSTRAINT fk_category FOREIGN KEY (category_id)
        REFERENCES categories (category_id) ON DELETE SET NULL
);

CREATE TABLE events_compilations (
    event_compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    CONSTRAINT pk_event_compilation PRIMARY KEY (event_compilation_id),
    CONSTRAINT fk_compilation_event FOREIGN KEY (event_id)
        REFERENCES events (event_id) ON DELETE CASCADE,
    CONSTRAINT fk_event_compilation  FOREIGN KEY (compilation_id)
        REFERENCES compilations (compilation_id) ON DELETE SET NULL
);

CREATE TABLE requests_participation (
    participation_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    confirmed BOOLEAN DEFAULT FALSE,
    moderation BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_participation PRIMARY KEY (participation_id),
    CONSTRAINT fk_event_request  FOREIGN KEY (event_id)
        REFERENCES events (event_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_request  FOREIGN KEY (user_id)
        REFERENCES users (user_id) ON DELETE CASCADE
);