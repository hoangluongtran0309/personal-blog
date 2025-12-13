CREATE TABLE projects (
    project_id UUID NOT NULL,
    project_category VARCHAR(255) NOT NULL,
    project_title VARCHAR(255) NOT NULL,
    project_summary TEXT NOT NULL,
    project_content TEXT NOT NULL,
    project_status VARCHAR(50) NOT NULL,
    project_version BIGINT NOT NULL,
    project_publish_date DATE,
    PRIMARY KEY (project_id)
);

