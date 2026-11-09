ALTER TABLE virtual_machines
    ADD COLUMN environment VARCHAR(20) DEFAULT 'prod',
    ADD COLUMN disk_type VARCHAR(10) DEFAULT 'ssd',
    ADD COLUMN network VARCHAR(50),
    ADD COLUMN backup_enabled BOOLEAN DEFAULT FALSE,
    ADD COLUMN description TEXT;