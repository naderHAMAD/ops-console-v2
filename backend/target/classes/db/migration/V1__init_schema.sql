-- V1__init_schema.sql
-- Schéma initial : utilisateurs, rôles, permissions, régions, serveurs, jobs, VM, audit

CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- pour gen_random_uuid()

CREATE TABLE regions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    is_2fa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    totp_secret VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    role_id UUID REFERENCES roles(id),
    region_id UUID REFERENCES regions(id)
);

CREATE TABLE servers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hostname VARCHAR(150) NOT NULL UNIQUE,
    ip_address VARCHAR(45),
    jboss_version VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'unknown', -- up | down | unknown
    last_update TIMESTAMPTZ,
    region_id UUID NOT NULL REFERENCES regions(id)
);

CREATE TABLE update_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    server_id UUID NOT NULL REFERENCES servers(id),
    triggered_by UUID NOT NULL REFERENCES users(id),
    target_version VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending', -- pending | running | success | failed | rolled_back
    logs TEXT,
    awx_job_id VARCHAR(50),
    started_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ
);

CREATE TABLE virtual_machines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL UNIQUE,
    cpu INT NOT NULL,
    ram_gb INT NOT NULL,
    disk_gb INT NOT NULL,
    template VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'pending', -- pending | active | failed | deleted
    region_id UUID NOT NULL REFERENCES regions(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE vm_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vm_id UUID NOT NULL REFERENCES virtual_machines(id),
    triggered_by UUID NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    logs TEXT,
    awx_job_id VARCHAR(50),
    started_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ
);

CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(50),
    target_id UUID,
    details TEXT,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_servers_region ON servers(region_id);
CREATE INDEX idx_update_jobs_server ON update_jobs(server_id);
CREATE INDEX idx_vm_region ON virtual_machines(region_id);
CREATE INDEX idx_audit_user ON audit_log(user_id);
