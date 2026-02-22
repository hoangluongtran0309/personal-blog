CREATE TABLE users (
    id              UUID            NOT NULL,
    username        VARCHAR(255)    NOT NULL UNIQUE,
    firstname       VARCHAR(255),
    lastname        VARCHAR(255),
    password        VARCHAR(255)    NOT NULL,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    avatar          BYTEA,
    gender          VARCHAR(20),
    date_of_birth   DATE,
    headline        VARCHAR(255),
    bio             TEXT,
    phone_number    VARCHAR(50),
    street          VARCHAR(255),
    city            VARCHAR(255),
    country         VARCHAR(255),
    github_url      VARCHAR(500),
    x_url           VARCHAR(500),
    facebook_url    VARCHAR(500),
    linkedin_url    VARCHAR(500),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE user_roles (
    user_id UUID         NOT NULL,
    roles   VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_users_email
    ON users (email);

CREATE INDEX idx_users_username
    ON users (username);

CREATE INDEX idx_user_roles_user_id
    ON user_roles (user_id);