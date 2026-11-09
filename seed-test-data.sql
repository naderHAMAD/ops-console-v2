-- 3 serveurs de test (ou mise a jour s'ils existent deja)
INSERT INTO servers (hostname, ip_address, jboss_version, status, region_id)
SELECT 'jb-nord-04.steg.tn', '10.10.1.14', '7.2.1', 'up', id FROM regions WHERE name = 'NORD'
UNION ALL
SELECT 'jb-nord-05.steg.tn', '10.10.1.15', '7.3.0', 'up', id FROM regions WHERE name = 'NORD'
UNION ALL
SELECT 'jb-sud-01.steg.tn', '10.20.1.11', '7.1.9', 'up', id FROM regions WHERE name = 'SUD'
ON CONFLICT (hostname) DO UPDATE
SET ip_address = EXCLUDED.ip_address,
    jboss_version = EXCLUDED.jboss_version,
    status = 'up',
    region_id = EXCLUDED.region_id;

-- 2 livraisons supplementaires
INSERT INTO releases (version, label, is_stable) VALUES
    ('7.4.1', 'Stable - 7.4.1', TRUE),
    ('7.5.0', 'Stable - 7.5.0', TRUE)
ON CONFLICT (version) DO NOTHING;

SELECT hostname, ip_address, jboss_version FROM servers ORDER BY hostname;
SELECT version, label FROM releases ORDER BY created_at;
