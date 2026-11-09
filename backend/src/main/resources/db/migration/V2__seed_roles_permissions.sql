-- V2__seed_roles_permissions.sql
-- Données de référence : régions, permissions, rôles, et un compte Super Admin initial

INSERT INTO regions (name) VALUES ('NORD'), ('SUD'), ('NATIONAL');

INSERT INTO permissions (code, description) VALUES
    ('jboss.update.trigger', 'Déclencher une mise à jour JBoss'),
    ('jboss.update.rollback', 'Effectuer un rollback manuel'),
    ('vm.create', 'Créer une machine virtuelle'),
    ('vm.delete', 'Supprimer une machine virtuelle'),
    ('user.manage', 'Gérer les utilisateurs et leurs rôles'),
    ('logs.view', 'Consulter les journaux d''audit'),
    ('logs.export', 'Exporter les journaux d''audit');

-- Rôles
INSERT INTO roles (name, description) VALUES
    ('SUPER_ADMIN', 'Accès complet à toute l''application'),
    ('REGIONAL_ADMIN', 'Gestion complète d''une région donnée'),
    ('OPERATOR', 'Déclenche les mises à jour et rollbacks'),
    ('VIEWER', 'Lecture seule, consultation et export');

-- Attribution des permissions par rôle
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'SUPER_ADMIN';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'REGIONAL_ADMIN'
  AND p.code IN ('jboss.update.trigger', 'jboss.update.rollback', 'vm.create', 'vm.delete', 'logs.view');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'OPERATOR'
  AND p.code IN ('jboss.update.trigger', 'jboss.update.rollback');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'VIEWER'
  AND p.code IN ('logs.view', 'logs.export');

-- Compte Super Admin initial
-- Mot de passe par défaut : "ChangeMe123!" (hash BCrypt) — À CHANGER dès la première connexion
INSERT INTO users (email, password_hash, full_name, is_active, role_id, region_id)
SELECT
    'admin@steg.tn',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5L1jSXrN/YtVfnHqNBQlHK6ijeFNe',
    'Super Administrateur',
    TRUE,
    r.id,
    reg.id
FROM roles r, regions reg
WHERE r.name = 'SUPER_ADMIN' AND reg.name = 'NATIONAL';
