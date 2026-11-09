CREATE TABLE releases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version VARCHAR(50) NOT NULL UNIQUE,
    label VARCHAR(100) NOT NULL,
    is_stable BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO releases (version, label, is_stable) VALUES
    ('7.4.0-alpha1', 'Alpha 1 — 7.4.0', FALSE),
    ('7.4.0-alpha2', 'Alpha 2 — 7.4.0', FALSE),
    ('7.4.0-alpha3', 'Alpha 3 — 7.4.0', FALSE),
    ('7.4.0', 'Stable — 7.4.0', TRUE);