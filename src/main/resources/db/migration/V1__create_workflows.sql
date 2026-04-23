CREATE TABLE workflows (
    id                           VARCHAR(36)  PRIMARY KEY,
    name                         VARCHAR(255) NOT NULL,
    natural_language_description TEXT,
    graph_json                   JSONB,
    status                       VARCHAR(50)  NOT NULL DEFAULT 'DRAFT',
    created_at                   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at                   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE workflow_executions (
    id          VARCHAR(36)  PRIMARY KEY,
    workflow_id VARCHAR(36)  NOT NULL REFERENCES workflows(id),
    status      VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    steps_log   JSONB,
    error_message TEXT,
    started_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    finished_at TIMESTAMPTZ
);

CREATE INDEX idx_executions_workflow_id ON workflow_executions(workflow_id);
CREATE INDEX idx_workflows_status ON workflows(status);