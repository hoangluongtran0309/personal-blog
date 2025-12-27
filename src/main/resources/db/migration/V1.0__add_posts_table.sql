CREATE TABLE posts (
    post_id UUID NOT NULL,
    post_title VARCHAR(255) NOT NULL,
    post_summary TEXT NOT NULL,
    post_content TEXT NOT NULL,
    post_status VARCHAR(50) NOT NULL,
    post_publish_date DATE,
    post_version BIGINT NOT NULL,
    PRIMARY KEY (post_id)
);
